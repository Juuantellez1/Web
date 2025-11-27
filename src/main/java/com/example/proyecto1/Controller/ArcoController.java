package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.ArcoDto;
import com.example.proyecto1.Service.ArcoService;
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
@RequestMapping("/api/procesos/{procesoId}/arcos")
@CrossOrigin(origins = "http://localhost:4200")
public class ArcoController {

    private final ArcoService arcoService;

    private void validarAutenticacion(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
    }

    @GetMapping
    public ResponseEntity<List<ArcoDto>> listarPorProceso(
            Authentication authentication,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(arcoService.listarPorProceso(procesoId));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ArcoDto>> listarActivosPorProceso(
            Authentication authentication,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(arcoService.listarActivosPorProceso(procesoId));
    }

    @GetMapping("/{arcoId}")
    public ResponseEntity<ArcoDto> obtener(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long arcoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(arcoService.obtenerPorId(procesoId, arcoId));
    }

    @PostMapping
    public ResponseEntity<ArcoDto> crear(
            Authentication authentication,
            @PathVariable Long procesoId,
            @Valid @RequestBody ArcoDto dto) {
        validarAutenticacion(authentication);
        dto.setProcesoId(procesoId);
        ArcoDto creado = arcoService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/procesos/" + procesoId + "/arcos/" + creado.getId()))
                .body(creado);
    }

    @PutMapping("/{arcoId}")
    public ResponseEntity<ArcoDto> actualizar(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long arcoId,
            @Valid @RequestBody ArcoDto dto) {
        validarAutenticacion(authentication);
        dto.setProcesoId(procesoId);
        return ResponseEntity.ok(arcoService.actualizar(procesoId, arcoId, dto));
    }

    @DeleteMapping("/{arcoId}")
    public ResponseEntity<Void> eliminar(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long arcoId) {
        validarAutenticacion(authentication);
        arcoService.eliminar(procesoId, arcoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{arcoId}/reactivar")
    public ResponseEntity<ArcoDto> reactivar(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long arcoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(arcoService.reactivar(procesoId, arcoId));
    }

    @DeleteMapping("/{arcoId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long arcoId) {
        validarAutenticacion(authentication);
        arcoService.eliminarFisicamente(procesoId, arcoId);
        return ResponseEntity.noContent().build();
    }
}