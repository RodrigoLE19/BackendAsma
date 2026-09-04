package com.Rodrigo.RespiraFacilAPI.dto;

public record RegistroUsuarioDTO(
        String nombre,
        String apellido,
        String email,
        String contrasena
) {
}
