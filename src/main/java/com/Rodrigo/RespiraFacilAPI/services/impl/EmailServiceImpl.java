package com.Rodrigo.RespiraFacilAPI.services.impl;

import com.Rodrigo.RespiraFacilAPI.services.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements IEmailService {
    private final JavaMailSender javaMailSender;


    public void enviarCorreoRecuperacion(
            String destinatario,
            String enlaceRecuperacion
    ) {
        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setTo(destinatario);
        mensaje.setSubject("Recuperación de contraseña - RespiraFacil");

        mensaje.setText(
                        "Hola,\n\n" +
                        "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                        "Ingresa al siguiente enlace:\n" +
                        enlaceRecuperacion +
                        "\n\nEste enlace expirará en 30 minutos.\n\n" +
                        "Si no solicitaste este cambio, puedes ignorar este correo.\n\n" +
                        "RespiraFácil"
        );
        javaMailSender.send(mensaje);
    }
}
