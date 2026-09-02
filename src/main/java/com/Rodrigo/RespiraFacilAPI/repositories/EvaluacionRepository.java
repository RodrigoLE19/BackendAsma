package com.Rodrigo.RespiraFacilAPI.repositories;

import com.Rodrigo.RespiraFacilAPI.entities.Evaluacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {

    @Query(value = "select * from evaluaciones where idusuario =:idUsuario", nativeQuery = true)
    public List<Evaluacion> obtenerEvaluacionesPorUsuario(@Param("idUsuario") Integer idUsuario);

}
