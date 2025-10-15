package com.example.proyecto1.Model;

import jakarta.persistence.*;
import lombok.*;

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

    public enum TipoGateway {
        EXCLUSIVO,
        INCLUSIVO,
        PARALELO
    }
}