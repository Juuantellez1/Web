package com.example.proyecto1.Mapper;

import com.example.proyecto1.Dto.LoginResponseDto;
import com.example.proyecto1.Dto.UsuarioDto;
import com.example.proyecto1.Model.Usuario;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "rol", target = "rolUsuario")
    @Mapping(target = "password", ignore = true) // No exponer password en DTO
    UsuarioDto toDto(Usuario usuario);

    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(source = "rolUsuario", target = "rol")
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    @Mapping(target = "ultimo_login", ignore = true)
    Usuario toEntity(UsuarioDto dto);

    List<UsuarioDto> toDtoList(List<Usuario> usuarios);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "empresa.nombre", target = "nombreEmpresa")
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "apellido", target = "apellido")
    @Mapping(source = "correo", target = "correo")
    @Mapping(source = "rol", target = "rolUsuario")
    @Mapping(target = "mensaje", constant = "Login exitoso")
    @Mapping(target = "exitoso", constant = "true")
    LoginResponseDto toLoginResponseDto(Usuario usuario);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fecha_registro", ignore = true)
    @Mapping(target = "fecha_modificacion", ignore = true)
    @Mapping(target = "ultimo_login", ignore = true)
    @Mapping(source = "rolUsuario", target = "rol")
    void updateEntityFromDto(UsuarioDto dto, @MappingTarget Usuario usuario);
}