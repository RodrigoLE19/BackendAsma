package com.Rodrigo.RespiraFacilAPI.services;

import com.Rodrigo.RespiraFacilAPI.dto.EvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.dto.ResponseEvaluacionDTO;

import java.util.List;

public interface IEvaluacionService {
    public List<ResponseEvaluacionDTO> obtenerEvaluacionesPorUsuario(Integer idUsuario);

    public ResponseEvaluacionDTO crearEvaluacion(EvaluacionDTO evaluacionDTO);
}
