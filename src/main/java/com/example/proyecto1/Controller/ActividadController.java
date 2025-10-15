package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.ActividadDto;
import com.example.proyecto1.Service.ActividadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/procesos/{procesoId}/actividades")
@CrossOrigin(origins = "http://localhost:4200")
public class ActividadController {

    private final ActividadService actividadService;

    @GetMapping
    public ResponseEntity<List<ActividadDto>> listarPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(actividadService.listarPorProceso(procesoId));
    }

    @GetMapping("/activas")
    public ResponseEntity<List<ActividadDto>> listarActivasPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(actividadService.listarActivasPorProceso(procesoId));
    }

    @GetMapping("/{actividadId}")
    public ResponseEntity<ActividadDto> obtener(
            @PathVariable Long procesoId,
            @PathVariable Long actividadId) {
        return ResponseEntity.ok(actividadService.obtenerPorId(procesoId, actividadId));
    }

    @PostMapping
    public ResponseEntity<ActividadDto> crear(
            @PathVariable Long procesoId,
            @Valid @RequestBody ActividadDto dto) {
        dto.setProcesoId(procesoId);
        ActividadDto creada = actividadService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/procesos/" + procesoId + "/actividades/" + creada.getId()))
                .body(creada);
    }

    @PutMapping("/{actividadId}")
    public ResponseEntity<ActividadDto> actualizar(
            @PathVariable Long procesoId,
            @PathVariable Long actividadId,
            @Valid @RequestBody ActividadDto dto) {
        dto.setProcesoId(procesoId);
        return ResponseEntity.ok(actividadService.actualizar(procesoId, actividadId, dto));
    }

    @DeleteMapping("/{actividadId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long procesoId,
            @PathVariable Long actividadId) {
        actividadService.eliminar(procesoId, actividadId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{actividadId}/reactivar")
    public ResponseEntity<ActividadDto> reactivar(
            @PathVariable Long procesoId,
            @PathVariable Long actividadId) {
        return ResponseEntity.ok(actividadService.reactivar(procesoId, actividadId));
    }

    @DeleteMapping("/{actividadId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(
            @PathVariable Long procesoId,
            @PathVariable Long actividadId) {
        actividadService.eliminarFisicamente(procesoId, actividadId);
        return ResponseEntity.noContent().build();
    }
}