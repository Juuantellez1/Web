package com.example.proyecto1.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "empresas")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "nit", nullable = false, unique = true, length = 30)
    private String nit;

    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro", nullable = false)
    private Timestamp fecha_registro;

    @Column(name = "fecha_modificacion", nullable = false)
    private Timestamp fecha_modificacion;
}