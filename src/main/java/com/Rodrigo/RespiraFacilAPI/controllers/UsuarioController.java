package com.Rodrigo.RespiraFacilAPI.controllers;
import com.Rodrigo.RespiraFacilAPI.dto.*;
import com.Rodrigo.RespiraFacilAPI.entities.Evaluacion;
import com.Rodrigo.RespiraFacilAPI.services.impl.EvaluacionServiceImpl;
import com.Rodrigo.RespiraFacilAPI.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.Rodrigo.RespiraFacilAPI.dto.RecuperarPasswordDTO;
import com.Rodrigo.RespiraFacilAPI.dto.ResetPasswordDTO;

@RequiredArgsConstructor
@RestController
@RequestMapping("usuarios")
public class UsuarioController {
    private final EvaluacionServiceImpl evaluacionService;
    private final UsuarioServiceImpl usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(
            @RequestBody RegistroUsuarioDTO registroUsuarioDTO){
        UsuarioResponseDTO usuario = usuarioService.registrarUsuario(registroUsuarioDTO);

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/auth")
    public ResponseEntity<UsuarioResponseDTO> authUsuario(
            @RequestBody AuthUsuarioDTO authUsuarioDTO){
        return ResponseEntity.ok(
                usuarioService.authUsuario(authUsuarioDTO)
        );
    }

    @GetMapping("/{id}/evaluaciones")
    public ResponseEntity obtenerEvaluaciones (@PathVariable(name = "id")Integer idUsuario) {
        List<ResponseEvaluacionDTO> evaluacions= evaluacionService.obtenerEvaluacionesPorUsuario(idUsuario);
        return ResponseEntity.ok(evaluacions);
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
