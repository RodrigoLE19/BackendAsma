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

@RequiredArgsConstructor
@Service
public class EvaluacionServiceImpl implements IEvaluacionService {
    private final EvaluacionRepository evaluacionRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ResponseEvaluacionDTO> obtenerEvaluacionesPorUsuario(Integer idUsuario) {

        List<Evaluacion> evaluaciones =
                evaluacionRepository.obtenerEvaluacionesPorUsuario(idUsuario);

        return EvaluacionMapper.toResponseEvaluacionDTOList(evaluaciones);
    }

    @Transactional
    @Override
    public ResponseEvaluacionDTO crearEvaluacion(EvaluacionDTO evaluacionDTO) {
        Evaluacion evaluacionGuardada=
                evaluacionRepository.save(
                        EvaluacionMapper.toEvaluacionEntity(evaluacionDTO));
        return EvaluacionMapper.toResponseEvaluacionDTO(evaluacionGuardada);
    }
}
