package com.Rodrigo.RespiraFacilAPI.dto;

public record UsuarioResponseDTO(
        Integer idUsuario,
        String nombre,
        String apellido,
        String email
) {
}
