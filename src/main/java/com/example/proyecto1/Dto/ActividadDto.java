package com.example.proyecto1.Dto;

import com.example.proyecto1.Model.Actividad.TipoActividad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadDto {
    private Long id;

    @NotNull(message = "El ID del proceso es obligatorio")
    private Long procesoId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El tipo de actividad es obligatorio")
    private TipoActividad tipo;

    private Long rolResponsableId;

    private String rolResponsableNombre; // Solo para lectura

    private Boolean activo;

    private Timestamp fecha_registro;
    private Timestamp fecha_modificacion;
}