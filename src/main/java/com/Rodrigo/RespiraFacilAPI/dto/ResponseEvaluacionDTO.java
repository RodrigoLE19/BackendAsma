package com.Rodrigo.RespiraFacilAPI.dto;

public record ResponseEvaluacionDTO(
        Integer idEvaluacion,
        String fecha,
        String hora,
        String tiempoPrediccion,
        String resultado,
        Integer usuario
) {

}
