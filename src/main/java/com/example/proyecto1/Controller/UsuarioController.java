package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.UsuarioDto;
import com.example.proyecto1.Model.RolUsuario;
import com.example.proyecto1.Service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private void validarAutenticacion(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> listar(Authentication authentication) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<UsuarioDto>> listarPorEmpresa(Authentication authentication, @PathVariable Long empresaId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(usuarioService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> obtener(Authentication authentication, @PathVariable Long id) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/empresa/{empresaId}/usuario/{id}")
    public ResponseEntity<UsuarioDto> obtenerPorEmpresa(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long id) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(usuarioService.obtenerPorIdYEmpresa(empresaId, id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> crear(Authentication authentication, @Valid @RequestBody UsuarioDto dto) {
        validarAutenticacion(authentication);
        UsuarioDto creado = usuarioService.crear(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> actualizar(Authentication authentication, @PathVariable Long id, @Valid @RequestBody UsuarioDto dto) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    @PutMapping("/empresa/{empresaId}/usuario/{id}")
    public ResponseEntity<UsuarioDto> actualizarPorEmpresa(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDto dto) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(usuarioService.actualizarPorEmpresa(empresaId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication authentication, @PathVariable Long id) {
        validarAutenticacion(authentication);
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empresa/{empresaId}/usuario/{id}")
    public ResponseEntity<Void> eliminarPorEmpresa(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long id) {
        validarAutenticacion(authentication);
        usuarioService.eliminarPorEmpresa(empresaId, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/empresa/{empresaId}/usuario/{id}/rol")
    public ResponseEntity<UsuarioDto> cambiarRol(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long id,
            @RequestParam RolUsuario nuevoRolUsuario) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(usuarioService.cambiarRol(empresaId, id, nuevoRolUsuario));
    }
}