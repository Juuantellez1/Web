
package com.example.proyecto1.Dto;

import com.example.proyecto1.Model.Arco.TipoNodo;
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
public class ArcoDto {
    private Long id;

    @NotNull(message = "El ID del proceso es obligatorio")
    private Long procesoId;

    @NotNull(message = "El tipo de origen es obligatorio")
    private TipoNodo tipoOrigen;

    @NotNull(message = "El ID de origen es obligatorio")
    private Long origenId;

    @NotNull(message = "El tipo de destino es obligatorio")
    private TipoNodo tipoDestino;

    @NotNull(message = "El ID de destino es obligatorio")
    private Long destinoId;

    private String condicion;

    private Boolean activo;

    private Timestamp fecha_registro;
    private Timestamp fecha_modificacion;

    private String origenNombre;
    private String destinoNombre;
}