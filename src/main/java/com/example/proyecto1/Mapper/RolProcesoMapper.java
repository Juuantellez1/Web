package com.example.proyecto1.Mapper;

import com.example.proyecto1.Dto.RolProcesoDto;
import com.example.proyecto1.Model.RolProceso;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RolProcesoMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(target = "cantidadActividadesAsignadas", ignore = true)
    @Mapping(target = "procesosUtilizados", ignore = true)
    RolProcesoDto toDto(RolProceso rolProceso);

    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    RolProceso toEntity(RolProcesoDto dto);

    List<RolProcesoDto> toDtoList(List<RolProceso> rolesProceso);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    void updateEntityFromDto(RolProcesoDto dto, @MappingTarget RolProceso rolProceso);
}