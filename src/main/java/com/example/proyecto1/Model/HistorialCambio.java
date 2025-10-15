package com.example.proyecto1.Model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "historial_cambios")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistorialCambio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntidad tipoEntidad;

    @Column(nullable = false)
    private Long entidadId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAccion accion;

    @Column(columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(nullable = false)
    private Timestamp fecha;

    @PrePersist
    protected void onCreate() {
        fecha = new Timestamp(System.currentTimeMillis());
    }

    public enum TipoEntidad {
        PROCESO,
        ACTIVIDAD,
        ARCO,
        GATEWAY,
        ROL
    }

    public enum TipoAccion {
        CREAR,
        MODIFICAR,
        ELIMINAR,
        ACTIVAR,
        DESACTIVAR
    }
}