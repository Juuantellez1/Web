package com.example.proyecto1.Dto;

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
public class RolProcesoDto {
    private Long id;

    @NotNull(message = "El ID de empresa es obligatorio")
    private Long empresaId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private Boolean activo;

    private Timestamp fecha_registro;
    private Timestamp fecha_modificacion;

    private Long cantidadActividadesAsignadas;
    private List<String> procesosUtilizados;
}