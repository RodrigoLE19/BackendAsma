package com.Rodrigo.RespiraFacilAPI.services;

import com.Rodrigo.RespiraFacilAPI.dto.EvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.dto.ResponseEvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.entities.Evaluacion;

import java.util.List;

public interface IEvaluacionService {
    public List<Evaluacion> obtenerEvaluacionesPorUsuario(Integer idUsuario);


    public ResponseEvaluacionDTO crearEvaluacion(EvaluacionDTO evaluacionDTO);
}
