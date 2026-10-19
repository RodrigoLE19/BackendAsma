package com.Rodrigo.RespiraFacilAPI.controllers;
import com.Rodrigo.RespiraFacilAPI.dto.EvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.dto.ResponseEvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.services.impl.EvaluacionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;

import com.Rodrigo.RespiraFacilAPI.dto.SolicitudEvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.services.ProcesarEvaluacionService;
import jakarta.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("evaluaciones")
public class EvaluacionController {

    private final EvaluacionServiceImpl evaluacionService;
    private final ProcesarEvaluacionService procesarEvaluacionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> guardarEvaluacion(
            @Valid @RequestBody SolicitudEvaluacionDTO solicitud
    ) {
        return ResponseEntity.ok(
                procesarEvaluacionService.procesar(solicitud)
        );
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ResponseEvaluacionDTO>> obtenerEvaluacionesPorUsuario(
            @PathVariable Integer idUsuario) {

        List<ResponseEvaluacionDTO> evaluaciones =
                evaluacionService.obtenerEvaluacionesPorUsuario(idUsuario);

        return ResponseEntity.ok(evaluaciones);

    }

}
