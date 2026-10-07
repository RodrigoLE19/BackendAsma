package com.Rodrigo.RespiraFacilAPI.services.impl;

import com.Rodrigo.RespiraFacilAPI.services.IEmailService;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.ByteArrayOutputStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailServiceImpl implements IEmailService {

    private final RestClient restClient;

    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final String remitente;

    public EmailServiceImpl(
            @Value("${GMAIL_CLIENT_ID}") String clientId,
            @Value("${GMAIL_CLIENT_SECRET}") String clientSecret,
            @Value("${GMAIL_REFRESH_TOKEN}") String refreshToken,
            @Value("${GMAIL_FROM}") String remitente
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
        this.remitente = remitente;

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        JdkClientHttpRequestFactory factory =
                new JdkClientHttpRequestFactory(httpClient);

        factory.setReadTimeout(Duration.ofSeconds(20));

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @Override
    public void enviarCorreoRecuperacion(
            String destinatario,
            String enlaceRecuperacion
    ) {
        try {
            MimeMessage mensaje =
                    new MimeMessage(Session.getInstance(new Properties()));

            InternetAddress direccionDestino =
                    new InternetAddress(destinatario, true);
            direccionDestino.validate();

            mensaje.setFrom(
                    new InternetAddress(remitente, "Respira Fácil", "UTF-8")
            );

            mensaje.setRecipient(
                    Message.RecipientType.TO,
                    direccionDestino
            );

            mensaje.setSubject(
                    "Recuperación de contraseña - Respira Fácil",
                    "UTF-8"
            );

            mensaje.setText(
                    "Hola,\n\n"
                            + "Recibimos una solicitud para restablecer "
                            + "tu contraseña.\n\n"
                            + "Ingresa al siguiente enlace:\n"
                            + enlaceRecuperacion
                            + "\n\nEste enlace expirará en 30 minutos.\n\n"
                            + "Si no solicitaste este cambio, "
                            + "puedes ignorar este correo.\n\n"
                            + "Respira Fácil",
                    "UTF-8"
            );

            mensaje.saveChanges();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            mensaje.writeTo(buffer);

            String mensajeCodificado = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(buffer.toByteArray());

            String accessToken = obtenerAccessToken();

            restClient.post()
                    .uri(
                            "https://gmail.googleapis.com/gmail/v1/"
                                    + "users/me/messages/send"
                    )
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("raw", mensajeCodificado))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException error) {
            // No incluir tokens ni el contenido del correo en los errores.
            throw new IllegalStateException(
                    "Google rechazó la operación de correo. Código HTTP: "
                            + error.getStatusCode().value()
            );
        } catch (Exception error) {
            throw new IllegalStateException(
                    "No se pudo enviar el correo de recuperación."
            );
        }
    }

    private String obtenerAccessToken() {
        LinkedMultiValueMap<String, String> formulario =
                new LinkedMultiValueMap<>();

        formulario.add("client_id", clientId);
        formulario.add("client_secret", clientSecret);
        formulario.add("refresh_token", refreshToken);
        formulario.add("grant_type", "refresh_token");

        Map<String, Object> respuesta = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formulario)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                Map<String, Object>
                                >() {}
                );

        if (respuesta == null
                || !(respuesta.get("access_token") instanceof String token)
                || token.isBlank()) {
            throw new IllegalStateException(
                    "Google no devolvió un token de acceso."
            );
        }

        return token;
    }
}
