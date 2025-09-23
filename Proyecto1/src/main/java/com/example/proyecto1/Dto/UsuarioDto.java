package com.example.proyecto1.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El correo debe tener un formato válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Size(min = 6, max = 100, message = "El password debe tener entre 6 y 100 caracteres")
    @NotBlank(message = "El password es obligatorio")
    private String password;

    private Boolean activo;

    private Timestamp ultimo_login;
    private Timestamp fecha_registro;
    private Timestamp fecha_modificacion;
}