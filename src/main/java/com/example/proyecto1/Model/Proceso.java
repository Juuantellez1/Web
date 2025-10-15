package com.example.proyecto1.Model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "procesos")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Proceso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProceso estado = EstadoProceso.BORRADOR;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Timestamp fecha_registro;

    @Column(nullable = false)
    private Timestamp fecha_modificacion;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Actividad> actividades;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Arco> arcos;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gateway> gateways;

    @PrePersist
    protected void onCreate() {
        fecha_registro = new Timestamp(System.currentTimeMillis());
        fecha_modificacion = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        fecha_modificacion = new Timestamp(System.currentTimeMillis());
    }

    public enum EstadoProceso {
        BORRADOR,
        PUBLICADO
    }
}