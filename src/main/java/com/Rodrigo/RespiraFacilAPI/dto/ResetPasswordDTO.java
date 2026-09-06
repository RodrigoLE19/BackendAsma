package com.Rodrigo.RespiraFacilAPI.dto;

public record ResetPasswordDTO(
        String token,
        String nuevaContrasena
) {
}
