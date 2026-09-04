package com.Rodrigo.RespiraFacilAPI.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "idusuario")
    private Integer idUsuario;

    private String nombre;

    private String apellido;

    private String email;

    private String contrasena;
}
