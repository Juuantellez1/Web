
package com.example.proyecto1.Dto;

import com.example.proyecto1.Model.RolUsuario;
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
    private Long empresaId;
    private String nombreEmpresa;
    private String nombre;
    private String apellido;
    private String correo;
    private RolUsuario rolUsuario;
    private String mensaje;
    private boolean exitoso;
    private String token;
}