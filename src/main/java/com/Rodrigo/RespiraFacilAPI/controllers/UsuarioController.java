package com.Rodrigo.RespiraFacilAPI.controllers;
import com.Rodrigo.RespiraFacilAPI.dto.AuthUsuarioDTO;
import com.Rodrigo.RespiraFacilAPI.dto.RegistroUsuarioDTO;
import com.Rodrigo.RespiraFacilAPI.dto.ResponseEvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.dto.UsuarioResponseDTO;
import com.Rodrigo.RespiraFacilAPI.entities.Evaluacion;
import com.Rodrigo.RespiraFacilAPI.services.impl.EvaluacionServiceImpl;
import com.Rodrigo.RespiraFacilAPI.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}
