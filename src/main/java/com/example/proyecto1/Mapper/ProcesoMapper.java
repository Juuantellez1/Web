package com.example.proyecto1.Mapper;

import com.example.proyecto1.Dto.ProcesoDetalleDto;
import com.example.proyecto1.Dto.ProcesoDto;
import com.example.proyecto1.Model.Proceso;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ActividadMapper.class, ArcoMapper.class, GatewayMapper.class})
public interface ProcesoMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(target = "cantidadActividades", ignore = true)
    @Mapping(target = "cantidadArcos", ignore = true)
    @Mapping(target = "cantidadGateways", ignore = true)
    ProcesoDto toDto(Proceso proceso);

    @Mapping(source = "empresa.id", target = "empresaId")
    ProcesoDetalleDto toDetalleDto(Proceso proceso);

    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    @Mapping(target = "actividades", ignore = true)
    @Mapping(target = "arcos", ignore = true)
    @Mapping(target = "gateways", ignore = true)
    Proceso toEntity(ProcesoDto dto);

    List<ProcesoDto> toDtoList(List<Proceso> procesos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    @Mapping(target = "actividades", ignore = true)
    @Mapping(target = "arcos", ignore = true)
    @Mapping(target = "gateways", ignore = true)
    void updateEntityFromDto(ProcesoDto dto, @MappingTarget Proceso proceso);
}