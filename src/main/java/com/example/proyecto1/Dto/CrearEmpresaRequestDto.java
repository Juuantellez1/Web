package com.example.proyecto1.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearEmpresaRequestDto {
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String nombreEmpresa;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 30, message = "El NIT no debe superar 30 caracteres")
    private String nit;

    @Email(message = "El correo de la empresa debe tener un formato válido")
    @NotBlank(message = "El correo de la empresa es obligatorio")
    private String correoEmpresa;

    @NotBlank(message = "El nombre del administrador es obligatorio")
    private String nombreAdmin;

    @NotBlank(message = "El apellido del administrador es obligatorio")
    private String apellidoAdmin;

    @Email(message = "El correo del administrador debe tener un formato válido")
    @NotBlank(message = "El correo del administrador es obligatorio")
    private String correoAdmin;

    @Size(min = 6, max = 100, message = "El password debe tener entre 6 y 100 caracteres")
    @NotBlank(message = "El password es obligatorio")
    private String passwordAdmin;
}