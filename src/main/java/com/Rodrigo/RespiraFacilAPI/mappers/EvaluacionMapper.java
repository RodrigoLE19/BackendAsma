package com.Rodrigo.RespiraFacilAPI.mappers;
import com.Rodrigo.RespiraFacilAPI.dto.EvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.dto.ResponseEvaluacionDTO;
import com.Rodrigo.RespiraFacilAPI.entities.Evaluacion;
import com.Rodrigo.RespiraFacilAPI.entities.Usuario;
import java.util.List;

public class EvaluacionMapper {
    public static Evaluacion toEvaluacionEntity(EvaluacionDTO evaluacionDTO) {
        Evaluacion evaluacion=new Evaluacion();
        Usuario usuario=new Usuario();
        usuario.setIdUsuario(evaluacionDTO.usuario());
        evaluacion.setFecha(evaluacionDTO.fecha());
        evaluacion.setHora(evaluacionDTO.hora());
        evaluacion.setTiempoprediccion(evaluacionDTO.tiempoPrediccion());
        evaluacion.setResultado(evaluacionDTO.resultado());
        evaluacion.setUsuario(usuario);
        return evaluacion;
    }

    public static ResponseEvaluacionDTO toResponseEvaluacionDTO(Evaluacion evaluacion1) {
        ResponseEvaluacionDTO responseEvaluacionDTO= new ResponseEvaluacionDTO(
                evaluacion1.getIdEvaluaciones(),
                evaluacion1.getFecha(),
                evaluacion1.getHora(),
                evaluacion1.getTiempoprediccion(),
                evaluacion1.getResultado(),
                evaluacion1.getUsuario().getIdUsuario());
        return responseEvaluacionDTO;
    }

    public static List<ResponseEvaluacionDTO> toResponseEvaluacionDTOList(
            List<Evaluacion> evaluaciones) {

        return evaluaciones.stream()
                .map(EvaluacionMapper::toResponseEvaluacionDTO)
                .toList();
    }

}
