package com.example.proyecto1.Mapper;

import com.example.proyecto1.Dto.EmpresaDto;
import com.example.proyecto1.Model.Empresa;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmpresaMapper {

    EmpresaDto toDto(Empresa empresa);

    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    Empresa toEntity(EmpresaDto dto);

    List<EmpresaDto> toDtoList(List<Empresa> empresas);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    void updateEntityFromDto(EmpresaDto dto, @MappingTarget Empresa empresa);
}