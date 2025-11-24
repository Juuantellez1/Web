package com.example.proyecto1.Dto;

import com.example.proyecto1.Model.Gateway.TipoGateway;
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
public class GatewayDto {
    private Long id;

    @NotNull(message = "El ID del proceso es obligatorio")
    private Long procesoId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El tipo de gateway es obligatorio")
    private TipoGateway tipo;

    private Boolean activo;

    private Timestamp fecha_registro;
    private Timestamp fecha_modificacion;

    private Long cantidadArcosEntrantes;
    private Long cantidadArcosSalientes;

    private Double x;
    private Double y;
}