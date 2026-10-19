package com.Rodrigo.RespiraFacilAPI.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


public record SolicitudEvaluacionDTO(
        @NotNull @DecimalMin("20") @DecimalMax("300")
        Double peso,

        @NotNull @DecimalMin("80") @DecimalMax("250")
        Double altura,

        @NotNull Boolean dificultadRespirar,
        @NotNull Boolean tos,
        @NotNull Boolean silbidoPecho,
        @NotNull Boolean opresionPecho
) {
}
