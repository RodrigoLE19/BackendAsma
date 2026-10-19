package com.Rodrigo.RespiraFacilAPI.dto;

public record LoginResponseDTO(
        Integer idUsuario,
        String nombre,
        String apellido,
        String email,
        String token
) {
}
