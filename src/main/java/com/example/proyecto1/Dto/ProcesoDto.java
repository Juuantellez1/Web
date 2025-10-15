package com.example.proyecto1.Dto;

import com.example.proyecto1.Model.Proceso.EstadoProceso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcesoDto {
    private Long id;

    @NotNull(message = "El ID de empresa es obligatorio")
    private Long empresaId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private String categoria;

    @NotNull(message = "El estado es obligatorio")
    private EstadoProceso estado;

    private Boolean activo;

    private Timestamp fecha_registro;
    private Timestamp fecha_modificacion;

    private Long cantidadActividades;
    private Long cantidadArcos;
    private Long cantidadGateways;
}