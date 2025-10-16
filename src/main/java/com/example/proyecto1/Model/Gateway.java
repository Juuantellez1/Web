package com.example.proyecto1.Model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "gateways")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Gateway {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoGateway tipo;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Timestamp fecha_registro;

    @Column(nullable = false)
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

    public enum TipoGateway {
        EXCLUSIVO,
        INCLUSIVO,
        PARALELO
    }
}