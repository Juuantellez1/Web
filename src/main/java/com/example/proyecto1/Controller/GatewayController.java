package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.GatewayDto;
import com.example.proyecto1.Service.GatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/procesos/{procesoId}/gateways")
@CrossOrigin(origins = "http://localhost:4200")
public class GatewayController {

    private final GatewayService gatewayService;

    @GetMapping
    public ResponseEntity<List<GatewayDto>> listarPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(gatewayService.listarPorProceso(procesoId));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<GatewayDto>> listarActivosPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(gatewayService.listarActivosPorProceso(procesoId));
    }

    @GetMapping("/{gatewayId}")
    public ResponseEntity<GatewayDto> obtener(
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId) {
        return ResponseEntity.ok(gatewayService.obtenerPorId(procesoId, gatewayId));
    }

    @PostMapping
    public ResponseEntity<GatewayDto> crear(
            @PathVariable Long procesoId,
            @Valid @RequestBody GatewayDto dto) {
        dto.setProcesoId(procesoId);
        GatewayDto creado = gatewayService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/procesos/" + procesoId + "/gateways/" + creado.getId()))
                .body(creado);
    }

    @PutMapping("/{gatewayId}")
    public ResponseEntity<GatewayDto> actualizar(
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId,
            @Valid @RequestBody GatewayDto dto) {
        dto.setProcesoId(procesoId);
        return ResponseEntity.ok(gatewayService.actualizar(procesoId, gatewayId, dto));
    }

    @DeleteMapping("/{gatewayId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId) {
        gatewayService.eliminar(procesoId, gatewayId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{gatewayId}/reactivar")
    public ResponseEntity<GatewayDto> reactivar(
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId) {
        return ResponseEntity.ok(gatewayService.reactivar(procesoId, gatewayId));
    }

    @DeleteMapping("/{gatewayId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId) {
        gatewayService.eliminarFisicamente(procesoId, gatewayId);
        return ResponseEntity.noContent().build();
    }
}