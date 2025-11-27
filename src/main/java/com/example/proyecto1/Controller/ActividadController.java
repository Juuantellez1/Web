package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.ActividadDto;
import com.example.proyecto1.Service.ActividadService;
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
@RequestMapping("/api/procesos/{procesoId}/actividades")
@CrossOrigin(origins = "http://localhost:4200")
public class ActividadController {

    private final ActividadService actividadService;

    private void validarAutenticacion(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
    }

    @GetMapping
    public ResponseEntity<List<ActividadDto>> listarPorProceso(
            Authentication authentication,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(actividadService.listarPorProceso(procesoId));
    }

    @GetMapping("/activas")
    public ResponseEntity<List<ActividadDto>> listarActivasPorProceso(
            Authentication authentication,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(actividadService.listarActivasPorProceso(procesoId));
    }

    @GetMapping("/{actividadId}")
    public ResponseEntity<ActividadDto> obtener(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long actividadId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(actividadService.obtenerPorId(procesoId, actividadId));
    }

    @PostMapping
    public ResponseEntity<ActividadDto> crear(
            Authentication authentication,
            @PathVariable Long procesoId,
            @Valid @RequestBody ActividadDto dto) {
        validarAutenticacion(authentication);
        dto.setProcesoId(procesoId);
        ActividadDto creada = actividadService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/procesos/" + procesoId + "/actividades/" + creada.getId()))
                .body(creada);
    }

    @PutMapping("/{actividadId}")
    public ResponseEntity<ActividadDto> actualizar(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long actividadId,
            @Valid @RequestBody ActividadDto dto) {
        validarAutenticacion(authentication);
        dto.setProcesoId(procesoId);
        return ResponseEntity.ok(actividadService.actualizar(procesoId, actividadId, dto));
    }

    @DeleteMapping("/{actividadId}")
    public ResponseEntity<Void> eliminar(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long actividadId) {
        validarAutenticacion(authentication);
        actividadService.eliminar(procesoId, actividadId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{actividadId}/reactivar")
    public ResponseEntity<ActividadDto> reactivar(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long actividadId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(actividadService.reactivar(procesoId, actividadId));
    }

    @DeleteMapping("/{actividadId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long actividadId) {
        validarAutenticacion(authentication);
        actividadService.eliminarFisicamente(procesoId, actividadId);
        return ResponseEntity.noContent().build();
    }
}