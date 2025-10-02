package com.example.proyecto1.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearEmpresaResponseDto {
    private EmpresaDto empresa;
    private UsuarioDto usuarioAdmin;
    private String mensaje;
}