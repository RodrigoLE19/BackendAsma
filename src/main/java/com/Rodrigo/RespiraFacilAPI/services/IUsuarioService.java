package com.Rodrigo.RespiraFacilAPI.services;

import com.Rodrigo.RespiraFacilAPI.dto.AuthUsuarioDTO;
import com.Rodrigo.RespiraFacilAPI.dto.RegistroUsuarioDTO;
import com.Rodrigo.RespiraFacilAPI.dto.UsuarioResponseDTO;

public interface IUsuarioService {
    UsuarioResponseDTO registrarUsuario(RegistroUsuarioDTO registroUsuarioDTO);

    UsuarioResponseDTO authUsuario(AuthUsuarioDTO authUsuarioDTO);

}
