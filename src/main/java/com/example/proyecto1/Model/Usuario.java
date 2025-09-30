package com.example.proyecto1.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "correo")
    private String correo;

    @Column(name = "password")
    private String password;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "ultimo_login")
    private Timestamp ultimo_login;

    @Column(name = "fecha_registro")
    private Timestamp fecha_registro;

    @Column(name = "fecha_modificacion")
    private Timestamp fecha_modificacion;
}
