package com.example.proyecto1.Mapper;

import com.example.proyecto1.Dto.ArcoDto;
import com.example.proyecto1.Model.Arco;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArcoMapper {

    @Mapping(source = "proceso.id", target = "procesoId")
    @Mapping(target = "origenNombre", ignore = true)
    @Mapping(target = "destinoNombre", ignore = true)
    ArcoDto toDto(Arco arco);

    @Mapping(source = "procesoId", target = "proceso.id")
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    Arco toEntity(ArcoDto dto);

    List<ArcoDto> toDtoList(List<Arco> arcos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proceso", ignore = true)
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    void updateEntityFromDto(ArcoDto dto, @MappingTarget Arco arco);
}