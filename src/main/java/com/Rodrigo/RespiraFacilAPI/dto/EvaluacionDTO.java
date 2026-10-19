package com.Rodrigo.RespiraFacilAPI.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record EvaluacionDTO(
     @NotBlank(message = "La fecha es obligatoria")
     @Pattern(
             regexp = "[0-9]{4}-[0-9]{2}-[0-9]{2}",
             message = "La fecha debe tener formato yyyy-MM-dd"
     )
     String fecha,

     @NotBlank(message = "La hora es obligatoria")
      @Pattern(
              regexp = "([01][0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?",
              message = "La hora debe tener formato HH:mm o HH:MM:SS"
      )
     String hora,

     @NotBlank(message = "El tiempo de predicción es obligatorio")
     @Pattern(
             regexp = "[0-9]{1,9}([.][0-9]{1,6})? ?ms",
             message = "El tiempo debe expersarse en milisegundos"
     )
     String tiempoPrediccion,

     @NotBlank(message = "El resultado es obligatorio")
     @Pattern(
             regexp = "Baja Probabilidad|Alta Probabilidad",
             message = "El resultado no es válido"
     )
     String resultado,

     @NotNull(message = "El usuario es obligatorio")
      @Positive(message = "El identificador del usuario debe ser positivo")
     Integer usuario
) {
}
