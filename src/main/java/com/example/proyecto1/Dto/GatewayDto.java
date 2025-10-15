package com.example.proyecto1.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatewayDto {
    private Long id;
    private Long procesoId;
    private String nombre;
    private String tipo;
    private String condicion;
    private Boolean activo;
}