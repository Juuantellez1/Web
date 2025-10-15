package com.example.proyecto1.Mapper;

import com.example.proyecto1.Dto.ActividadDto;
import com.example.proyecto1.Model.Actividad;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActividadMapper {

    @Mapping(source = "proceso.id", target = "procesoId")
    @Mapping(source = "rolResponsable.id", target = "rolResponsableId")
    @Mapping(source = "rolResponsable.nombre", target = "rolResponsableNombre")
    ActividadDto toDto(Actividad actividad);

    @Mapping(source = "procesoId", target = "proceso.id")
    @Mapping(source = "rolResponsableId", target = "rolResponsable.id")
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    @Mapping(target = "rolResponsableNombre", ignore = true)
    Actividad toEntity(ActividadDto dto);

    List<ActividadDto> toDtoList(List<Actividad> actividades);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proceso", ignore = true)
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    @Mapping(source = "rolResponsableId", target = "rolResponsable.id")
    void updateEntityFromDto(ActividadDto dto, @MappingTarget Actividad actividad);
}