package com.example.proyecto1.Controller;

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
        LoginResponseDto response = usuarioService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}