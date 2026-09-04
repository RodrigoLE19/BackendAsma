package com.Rodrigo.RespiraFacilAPI.services.impl;
import com.Rodrigo.RespiraFacilAPI.dto.AuthUsuarioDTO;
import com.Rodrigo.RespiraFacilAPI.dto.RegistroUsuarioDTO;
import com.Rodrigo.RespiraFacilAPI.dto.UsuarioResponseDTO;
import com.Rodrigo.RespiraFacilAPI.entities.Usuario;
import com.Rodrigo.RespiraFacilAPI.repositories.UsuarioRepository;
import com.Rodrigo.RespiraFacilAPI.services.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
}
