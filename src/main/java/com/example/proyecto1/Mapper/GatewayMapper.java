package com.example.proyecto1.Mapper;

import com.example.proyecto1.Dto.GatewayDto;
import com.example.proyecto1.Model.Gateway;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GatewayMapper {

    @Mapping(source = "proceso.id", target = "procesoId")
    @Mapping(target = "cantidadArcosEntrantes", ignore = true)
    @Mapping(target = "cantidadArcosSalientes", ignore = true)
    GatewayDto toDto(Gateway gateway);

    @Mapping(source = "procesoId", target = "proceso.id")
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    Gateway toEntity(GatewayDto dto);

    List<GatewayDto> toDtoList(List<Gateway> gateways);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proceso", ignore = true)
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    void updateEntityFromDto(GatewayDto dto, @MappingTarget Gateway gateway);
}