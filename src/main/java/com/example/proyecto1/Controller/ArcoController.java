package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.ArcoDto;
import com.example.proyecto1.Service.ArcoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/procesos/{procesoId}/arcos")
@CrossOrigin(origins = "http://localhost:4200")
public class ArcoController {

    private final ArcoService arcoService;

    @GetMapping
    public ResponseEntity<List<ArcoDto>> listarPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(arcoService.listarPorProceso(procesoId));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ArcoDto>> listarActivosPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(arcoService.listarActivosPorProceso(procesoId));
    }

    @GetMapping("/{arcoId}")
    public ResponseEntity<ArcoDto> obtener(
            @PathVariable Long procesoId,
            @PathVariable Long arcoId) {
        return ResponseEntity.ok(arcoService.obtenerPorId(procesoId, arcoId));
    }

    @PostMapping
    public ResponseEntity<ArcoDto> crear(
            @PathVariable Long procesoId,
            @Valid @RequestBody ArcoDto dto) {
        dto.setProcesoId(procesoId);
        ArcoDto creado = arcoService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/procesos/" + procesoId + "/arcos/" + creado.getId()))
                .body(creado);
    }

    @PutMapping("/{arcoId}")
    public ResponseEntity<ArcoDto> actualizar(
            @PathVariable Long procesoId,
            @PathVariable Long arcoId,
            @Valid @RequestBody ArcoDto dto) {
        dto.setProcesoId(procesoId);
        return ResponseEntity.ok(arcoService.actualizar(procesoId, arcoId, dto));
    }

    @DeleteMapping("/{arcoId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long procesoId,
            @PathVariable Long arcoId) {
        arcoService.eliminar(procesoId, arcoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{arcoId}/reactivar")
    public ResponseEntity<ArcoDto> reactivar(
            @PathVariable Long procesoId,
            @PathVariable Long arcoId) {
        return ResponseEntity.ok(arcoService.reactivar(procesoId, arcoId));
    }

    @DeleteMapping("/{arcoId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(
            @PathVariable Long procesoId,
            @PathVariable Long arcoId) {
        arcoService.eliminarFisicamente(procesoId, arcoId);
        return ResponseEntity.noContent().build();
    }
}