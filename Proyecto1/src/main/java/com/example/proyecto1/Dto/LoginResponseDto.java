package com.example.proyecto1.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String mensaje;
    private boolean exitoso;
}