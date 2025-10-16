
package com.example.proyecto1.Model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "arcos")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Arco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNodo tipoOrigen;

    @Column(nullable = false)
    private Long origenId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNodo tipoDestino;

    @Column(nullable = false)
    private Long destinoId;

    private String condicion;

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

    public enum TipoNodo {
        ACTIVIDAD,
        GATEWAY,
        EVENTO_INICIO,
        EVENTO_FIN
    }
}