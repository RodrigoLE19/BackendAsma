package com.Rodrigo.RespiraFacilAPI.services.impl;
import com.Rodrigo.RespiraFacilAPI.dto.EvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.dto.ResponseEvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.entities.Evaluacion;
import com.Rodrigo.RespiraFacilAPI.mappers.EvaluacionMapper;
import com.Rodrigo.RespiraFacilAPI.repositories.EvaluacionRepository;
import com.Rodrigo.RespiraFacilAPI.services.IEvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class EvaluacionServiceImpl implements IEvaluacionService {
    private final EvaluacionRepository evaluacionRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ResponseEvaluacionDTO> obtenerEvaluacionesPorUsuario(Integer idUsuario) {
        validarUsuarioAutenticado(idUsuario);

        List<Evaluacion> evaluaciones =
                evaluacionRepository.obtenerEvaluacionesPorUsuario(idUsuario);

        return EvaluacionMapper.toResponseEvaluacionDTOList(evaluaciones);
    }

    @Transactional
    @Override
    public ResponseEvaluacionDTO crearEvaluacion(EvaluacionDTO evaluacionDTO) {
        validarUsuarioAutenticado(evaluacionDTO.usuario());

        Evaluacion evaluacionGuardada=
                evaluacionRepository.save(
                        EvaluacionMapper.toEvaluacionEntity(evaluacionDTO));

        return EvaluacionMapper.toResponseEvaluacionDTO(evaluacionGuardada);
    }

    private void validarUsuarioAutenticado(Integer idUsuario) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión"
            );
        }

        if (idUsuario == null
                || !idUsuario.toString().equals(jwt.getSubject())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes acceder a evaluaciones de otro usuario"
            );
        }
    }
}
