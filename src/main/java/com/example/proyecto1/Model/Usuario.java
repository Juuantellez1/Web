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
@Table(
        name = "usuarios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"empresa_id", "correo"})
)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "correo", nullable = false)
    private String correo;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private RolUsuario rol = RolUsuario.LECTOR;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "ultimo_login")
    private Timestamp ultimo_login;

    @Column(name = "fecha_registro", nullable = false)
    private Timestamp fecha_registro;

    @Column(name = "fecha_modificacion", nullable = false)
    private Timestamp fecha_modificacion;

    @PrePersist
    protected void onCreate() {
        fecha_registro = new Timestamp(System.currentTimeMillis());
        fecha_modificacion = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        fecha_modificacion = new Timestamp(System.currentTimeMillis());
    }
}