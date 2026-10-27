package com.Rodrigo.RespiraFacilAPI.controllers;
import com.Rodrigo.RespiraFacilAPI.dto.*;
import com.Rodrigo.RespiraFacilAPI.services.JwtService;
import com.Rodrigo.RespiraFacilAPI.services.impl.EvaluacionServiceImpl;
import com.Rodrigo.RespiraFacilAPI.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.Rodrigo.RespiraFacilAPI.dto.RecuperarPasswordDTO;
import com.Rodrigo.RespiraFacilAPI.dto.ResetPasswordDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("usuarios")
public class UsuarioController {
    private final EvaluacionServiceImpl evaluacionService;
    private final UsuarioServiceImpl usuarioService;
    private final JwtService jwtService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(
            @RequestBody RegistroUsuarioDTO registroUsuarioDTO){
        UsuarioResponseDTO usuario = usuarioService.registrarUsuario(registroUsuarioDTO);

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/auth")
    public ResponseEntity<LoginResponseDTO> authUsuario(
            @RequestBody AuthUsuarioDTO authUsuarioDTO
    ){
        UsuarioResponseDTO usuario = usuarioService.authUsuario(authUsuarioDTO);

        String token = jwtService.generarToken(usuario.idUsuario());

        return ResponseEntity.ok(
                new LoginResponseDTO(
                        usuario.idUsuario(),
                        usuario.nombre(),
                        usuario.apellido(),
                        usuario.email(),
                        token
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obtenerMiPerfil(
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión"
            );
        }

        Integer idUsuario;

        try {
            idUsuario = Integer.valueOf(jwt.getSubject());
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "La identidad de la sesión no es válida"
            );
        }

        return ResponseEntity.ok(
                usuarioService.obtenerPerfilActual(idUsuario)
        );
    }

    @GetMapping("/{id}/evaluaciones")
    public ResponseEntity<List<ResponseEvaluacionDTO>> obtenerEvaluaciones (
            @PathVariable(name = "id")Integer idUsuario) {
        List<ResponseEvaluacionDTO> evaluaciones= evaluacionService.obtenerEvaluacionesPorUsuario(idUsuario);
        return ResponseEntity.ok(evaluaciones);
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<String> recuperarContrasena(
            @RequestBody RecuperarPasswordDTO recuperarPasswordDTO) {
        usuarioService.solicitarRecuperacionPassword(recuperarPasswordDTO);
        return ResponseEntity.ok("Solicitud de recuperación generada correctamente");
    }

    @PatchMapping("/restablecer-contrasena")
    public ResponseEntity<String> restablecerContrasena(
            @RequestBody ResetPasswordDTO resetPasswordDTO
    ){
        usuarioService.restablecerPassword(resetPasswordDTO);

        return ResponseEntity.ok("Contraseña restablecida correctamente");
    }
}
