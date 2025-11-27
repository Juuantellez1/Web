package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.AuthorizedDTO;
import com.example.proyecto1.Dto.LoginDto;
import com.example.proyecto1.Dto.LoginResponseDto;
import com.example.proyecto1.Service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        AuthorizedDTO authorized = usuarioService.login(loginDto);

        LoginResponseDto response = LoginResponseDto.builder()
                .id(authorized.getUser().getId())
                .empresaId(authorized.getUser().getEnterprise().getId())
                .nombreEmpresa(authorized.getUser().getEnterprise().getNombre())
                .nombre(authorized.getUser().getNombre())
                .apellido(authorized.getUser().getApellido())
                .correo(authorized.getUser().getCorreo())
                .rolUsuario(authorized.getUser().getRolUsuario())
                .token(authorized.getToken())
                .mensaje("Login exitoso")
                .exitoso(true)
                .build();

        return ResponseEntity.ok(response);
    }
}