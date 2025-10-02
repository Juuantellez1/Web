package com.example.proyecto1.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDto {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 30, message = "El NIT no debe superar 30 caracteres")
    private String nit;

    @Email(message = "El correo debe tener un formato válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    private Boolean activo;

    private Timestamp fecha_registro;
    private Timestamp fecha_modificacion;
}