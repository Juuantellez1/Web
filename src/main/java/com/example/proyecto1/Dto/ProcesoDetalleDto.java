package com.example.proyecto1.Dto;

import com.example.proyecto1.Model.Proceso.EstadoProceso;
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
public class ProcesoDetalleDto {
    private Long id;
    private Long empresaId;
    private String nombre;
    private String descripcion;
    private String categoria;
    private EstadoProceso estado;
    private Boolean activo;
    private Timestamp fecha_registro;
    private Timestamp fecha_modificacion;

    private List<ActividadDto> actividades;
    private List<ArcoDto> arcos;
    private List<GatewayDto> gateways;
}