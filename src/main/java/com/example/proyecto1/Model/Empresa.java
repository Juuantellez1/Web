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

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "nit")
    private String nit;

    @Column(name = "correo")
    private String correo;

    @Column(name = "fecha_registro")
    private Timestamp fecha_registro;

    @Column(name = "fecha_modificacion")
    private Timestamp fecha_modificacion;
}
