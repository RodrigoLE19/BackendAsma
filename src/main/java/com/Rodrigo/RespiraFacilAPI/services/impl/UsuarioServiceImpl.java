package com.Rodrigo.RespiraFacilAPI.services.impl;
import com.Rodrigo.RespiraFacilAPI.dto.*;
import com.Rodrigo.RespiraFacilAPI.entities.PasswordResetToken;
import com.Rodrigo.RespiraFacilAPI.entities.Usuario;
import com.Rodrigo.RespiraFacilAPI.repositories.PasswordResetTokenRepository;
import com.Rodrigo.RespiraFacilAPI.repositories.UsuarioRepository;
import com.Rodrigo.RespiraFacilAPI.services.IEmailService;
import com.Rodrigo.RespiraFacilAPI.services.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private  final PasswordResetTokenRepository passwordResetTokenRepository;
    private final IEmailService emailService;

    @Override
    public UsuarioResponseDTO registrarUsuario(RegistroUsuarioDTO registroUsuarioDTO) {

        if (usuarioRepository.findByEmail(registroUsuarioDTO.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El correo ya está registrado"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(registroUsuarioDTO.nombre());
        usuario.setApellido(registroUsuarioDTO.apellido());
        usuario.setEmail(registroUsuarioDTO.email());
        usuario.setContrasena(
                passwordEncoder.encode(registroUsuarioDTO.contrasena())
        );

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getApellido(),
                usuarioGuardado.getEmail()
        );
    }

    @Override
    public UsuarioResponseDTO authUsuario(AuthUsuarioDTO authUsuarioDTO) {
        Usuario usuario = usuarioRepository.findByEmail(authUsuarioDTO.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Correo o contraseña incorrectos"
                ));

        boolean contrasenaCorrecta = passwordEncoder.matches(
                authUsuarioDTO.contrasena(),
                usuario.getContrasena()
        );

        if (!contrasenaCorrecta) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectas"
            );
        }
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail()
        );
    }

    public  void solicitarRecuperacionPassword(RecuperarPasswordDTO recuperarPasswordDTO) {
        // Busca al susuario mediante su correo
        Usuario usuario = usuarioRepository.findByEmail(recuperarPasswordDTO.email())
                .orElse(null);

        if (usuario == null) {
            return;
        }

        // Genera un token único
        String token = UUID.randomUUID().toString();

        // Crea y guarda el token
        PasswordResetToken passwordResetToken = new PasswordResetToken();

        passwordResetToken.setToken(token);
        passwordResetToken.setUsuario(usuario);
        passwordResetToken.setFechaExpiracion(
                LocalDateTime.now().plusMinutes(30)
        );

        passwordResetTokenRepository.save(passwordResetToken);

        // Contruye el enlace que abrira angular
        String enlaceRecuperacion = "http://localhost:4200/reset-password/" + token;

        // Envia el correo
        emailService.enviarCorreoRecuperacion(
                usuario.getEmail(),
                enlaceRecuperacion
        );

    }

    public  void restablecerPassword(ResetPasswordDTO resetPasswordDTO) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findByToken(resetPasswordDTO.token())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El enlace de recuperación no es válido"));

        if (passwordResetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(passwordResetToken);

            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "El enlace de recuperación ha expirado");
        }

        Usuario usuario = passwordResetToken.getUsuario();

        usuario.setContrasena(passwordEncoder.encode(resetPasswordDTO.nuevaContrasena()));

        usuarioRepository.save(usuario);

        passwordResetTokenRepository.delete(passwordResetToken);
    }
}
