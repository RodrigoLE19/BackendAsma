package com.Rodrigo.RespiraFacilAPI.controllers;
import com.Rodrigo.RespiraFacilAPI.dto.EvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.dto.ResponseEvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.services.impl.EvaluacionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("evaluaciones")
public class EvaluacionController {

    private final EvaluacionServiceImpl evaluacionService;

    @PostMapping
    public ResponseEntity<ResponseEvaluacionDTO> guardarEvaluacion (
            @RequestBody EvaluacionDTO evaluacionDTO) {
        ResponseEvaluacionDTO evaluacionResponse =
                evaluacionService.crearEvaluacion(evaluacionDTO);
        return  ResponseEntity.ok(evaluacionResponse);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ResponseEvaluacionDTO>> obtenerEvaluacionesPorUsuario(
            @PathVariable Integer idUsuario) {

        List<ResponseEvaluacionDTO> evaluaciones =
                evaluacionService.obtenerEvaluacionesPorUsuario(idUsuario);

        return ResponseEntity.ok(evaluaciones);

    }

}
