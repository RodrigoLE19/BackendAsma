package com.Rodrigo.RespiraFacilAPI.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevaluaciones")
    private Integer idEvaluaciones;

    private String fecha;

    private String hora;

    private String tiempoprediccion;

    private String resultado;

    @ManyToOne
    @JoinColumn(name = "idusuario")
    private Usuario usuario;

}
