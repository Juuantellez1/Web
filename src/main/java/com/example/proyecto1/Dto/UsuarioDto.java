package com.example.proyecto1.Dto;

import com.example.proyecto1.Model.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private Long id;

    @NotNull(message = "El ID de empresa es obligatorio")
    private Long empresaId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El correo debe tener un formato válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Size(min = 6, max = 100, message = "El password debe tener entre 6 y 100 caracteres")
    private String password; // No obligatorio en actualizaciones

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rolUsuario;

    private Boolean activo;

    private Timestamp ultimo_login;
    private Timestamp fecha_registro;
    private Timestamp fecha_modificacion;
}