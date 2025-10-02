package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.UsuarioDto;
import com.example.proyecto1.Model.Rol;
import com.example.proyecto1.Service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<UsuarioDto>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(usuarioService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/empresa/{empresaId}/usuario/{id}")
    public ResponseEntity<UsuarioDto> obtenerPorEmpresa(
            @PathVariable Long empresaId,
            @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorIdYEmpresa(empresaId, id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody UsuarioDto dto) {
        UsuarioDto creado = usuarioService.crear(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDto dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    @PutMapping("/empresa/{empresaId}/usuario/{id}")
    public ResponseEntity<UsuarioDto> actualizarPorEmpresa(
            @PathVariable Long empresaId,
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDto dto) {
        return ResponseEntity.ok(usuarioService.actualizarPorEmpresa(empresaId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empresa/{empresaId}/usuario/{id}")
    public ResponseEntity<Void> eliminarPorEmpresa(
            @PathVariable Long empresaId,
            @PathVariable Long id) {
        usuarioService.eliminarPorEmpresa(empresaId, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/empresa/{empresaId}/usuario/{id}/rol")
    public ResponseEntity<UsuarioDto> cambiarRol(
            @PathVariable Long empresaId,
            @PathVariable Long id,
            @RequestParam Rol nuevoRol) {
        return ResponseEntity.ok(usuarioService.cambiarRol(empresaId, id, nuevoRol));
    }
}