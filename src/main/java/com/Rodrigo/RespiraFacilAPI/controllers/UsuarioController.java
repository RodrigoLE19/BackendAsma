package com.Rodrigo.RespiraFacilAPI.controllers;

import com.Rodrigo.RespiraFacilAPI.entities.Evaluacion;
import com.Rodrigo.RespiraFacilAPI.services.impl.EvaluacionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("usuarios")
public class UsuarioController {
    private final EvaluacionServiceImpl evaluacionService;

    @GetMapping("/{id}/evaluaciones")
    public ResponseEntity obtenerEvaluaciones (@PathVariable(name = "id")Integer idUsuario) {
        List<Evaluacion> evaluacions= evaluacionService.obtenerEvaluacionesPorUsuario(idUsuario);
        return ResponseEntity.ok(evaluacions);
    }
}
