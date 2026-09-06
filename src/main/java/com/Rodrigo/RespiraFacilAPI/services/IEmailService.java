package com.Rodrigo.RespiraFacilAPI.services;

public interface IEmailService {
    void enviarCorreoRecuperacion(
            String destinatario,
            String enlaceRecuperacion
    );
}
