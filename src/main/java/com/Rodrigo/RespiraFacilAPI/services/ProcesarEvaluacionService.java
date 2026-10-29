package com.Rodrigo.RespiraFacilAPI.services;

import com.Rodrigo.RespiraFacilAPI.dto.EvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.dto.SolicitudEvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.services.impl.EvaluacionServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;

@Service
public class ProcesarEvaluacionService {

    private static final Logger log =
            LoggerFactory.getLogger(ProcesarEvaluacionService.class);
    private final EvaluacionServiceImpl evaluacionService;
    private final RestClient modeloClient;


    public ProcesarEvaluacionService(
            EvaluacionServiceImpl evaluacionService,
            @Value("${app.modelo.url}") String modeloUrl
    ) {
        this.evaluacionService = evaluacionService;

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        JdkClientHttpRequestFactory factory =
                new JdkClientHttpRequestFactory(httpClient);

        factory.setReadTimeout(Duration.ofSeconds(120));

        this.modeloClient = RestClient.builder()
                .baseUrl(modeloUrl)
                .requestFactory(factory)
                .build();
    }

    public Map<String, Object> procesar(SolicitudEvaluacionDTO solicitud) {
        Integer idUsuario = obtenerIdUsuario();

        double alturaMetros = solicitud.altura() / 100.0;

        double imc = Math.round(
                solicitud.peso() / (alturaMetros * alturaMetros) * 100.0
        ) / 100.0;

        Map<String, Object> datosModelo = Map.of(
                "questionIMC", imc,
                "questionWheezing", solicitud.silbidoPecho() ? 1 : 0,
                "questionShortnessOfBreath", solicitud.dificultadRespirar() ? 1 : 0,
                "questionChestTightness", solicitud.opresionPecho() ? 1 : 0,
                "questionCoughing", solicitud.tos() ? 1 : 0
        );

        Instant inicio = Instant.now();

        Map<?, ?> respuesta;

        long inicioConsulta = System.nanoTime();

        try {
            respuesta = modeloClient.post()
                    .uri("/evaluation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(datosModelo)
                    .retrieve()
                    .body(Map.class);

        } catch (RestClientResponseException error) {
            long duracionMs = Duration.ofNanos(
                    System.nanoTime() - inicioConsulta
            ).toMillis();

            log.warn(
                    "El modelo respondió con HTTP {} después de {} ms",
                    error.getStatusCode().value(),
                    duracionMs
            );

            if (error.getStatusCode().value() == 429) {
                var headers = error.getResponseHeaders();

                log.warn(
                        "Modelo HTTP 429. Retry-After: {}. Server: {}. Content-Type: {}",
                        headers == null ? null : headers.getFirst("Retry-After"),
                        headers == null ? null : headers.getFirst("Server"),
                        headers == null ? null : headers.getFirst("Content-Type")
                );

                String cuerpo = error.getResponseBodyAsString()
                        .replace('\r', ' ')
                        .replace('\n', ' ');

                log.warn(
                        "Detalle del rechazo 429: {}",
                        cuerpo.substring(0, Math.min(cuerpo.length(), 600))
                );
            }


            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar el modelo de evaluación",
                    error
            );

        } catch (RestClientException error) {
            long duracionMs = Duration.ofNanos(
                    System.nanoTime() - inicioConsulta
            ).toMillis();

            Throwable causa = error.getMostSpecificCause();

            log.warn(
                    "Error al conectar con el modelo después de {} ms. Tipo: {}",
                    duracionMs,
                    causa.getClass().getSimpleName()
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar el modelo de evaluación",
                    error
            );
        }

        Instant fin = Instant.now();

        Object diagnosticoRecibido = respuesta == null
                ? null
                : respuesta.get("AsthmaDiagnosis");

        if (!(diagnosticoRecibido instanceof String diagnostico)
                || !(diagnostico.equals("[0]")
                || diagnostico.equals("[1]"))) {
            log.warn("El modelo devolvió un diagnóstico ausente o no válido");
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "El modelo devolvió un resultado no válido"
            );
        }

        Object tiempoRecibido = respuesta.get("prediction_time_ms");

        if (!(tiempoRecibido instanceof Number tiempo)
                || !Double.isFinite(tiempo.doubleValue())
                || tiempo.doubleValue() < 0) {
            log.warn("El modelo devolvió un tiempo de predicción ausente o no válido");
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "El modelo devolvió un tiempo de predicción no válido"
            );
        }

        long tiempoMs = Math.round(tiempo.doubleValue());

        String resultado = diagnostico.equals("[0]")
                ? "Baja Probabilidad"
                : "Alta Probabilidad";

        var fechaHora = fin.atZone(ZoneId.of("America/Lima"));

        EvaluacionDTO evaluacion = new EvaluacionDTO(
                fechaHora.toLocalDate().toString(),
                fechaHora.format(DateTimeFormatter.ofPattern("HH:mm")),
                tiempoMs + " ms",
                resultado,
                idUsuario
        );

        evaluacionService.crearEvaluacion(evaluacion);

        return Map.of(
                "AsthmaDiagnosis", diagnostico,
                "message", "Evaluación procesada correctamente",
                "prediction_time_ms", tiempoMs,
                "start_time", inicio.toString(),
                "end_time", fin.toString()
        );
    }

    private Integer obtenerIdUsuario() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión"
            );
        }

        try {
            int idUsuario = Integer.parseInt(jwt.getSubject());

            if (idUsuario <= 0) {
                throw new NumberFormatException();
            }

            return idUsuario;
        } catch (NumberFormatException error) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "El token no contiene un usuario válido"
            );
        }
    }
}