package com.Rodrigo.RespiraFacilAPI.dto;

public record EvaluacionDTO(
        String fecha,
        String hora,
        String tiempoPrediccion,
        String resultado,
        Integer usuario
) {
}
