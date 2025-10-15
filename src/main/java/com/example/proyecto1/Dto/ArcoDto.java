package com.example.proyecto1.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArcoDto {
    private Long id;
    private Long procesoId;
    private Long origenId;
    private String origenTipo;
    private Long destinoId;
    private String destinoTipo;
    private String condicion;
    private Boolean activo;
}