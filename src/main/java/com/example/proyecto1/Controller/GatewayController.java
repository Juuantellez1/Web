package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.GatewayDto;
import com.example.proyecto1.Service.GatewayService;
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
@RequestMapping("/api/procesos/{procesoId}/gateways")
@CrossOrigin(origins = "http://localhost:4200")
public class GatewayController {

    private final GatewayService gatewayService;

    private void validarAutenticacion(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
    }

    @GetMapping
    public ResponseEntity<List<GatewayDto>> listarPorProceso(
            Authentication authentication,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(gatewayService.listarPorProceso(procesoId));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<GatewayDto>> listarActivosPorProceso(
            Authentication authentication,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(gatewayService.listarActivosPorProceso(procesoId));
    }

    @GetMapping("/{gatewayId}")
    public ResponseEntity<GatewayDto> obtener(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(gatewayService.obtenerPorId(procesoId, gatewayId));
    }

    @PostMapping
    public ResponseEntity<GatewayDto> crear(
            Authentication authentication,
            @PathVariable Long procesoId,
            @Valid @RequestBody GatewayDto dto) {
        validarAutenticacion(authentication);
        dto.setProcesoId(procesoId);
        GatewayDto creado = gatewayService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/procesos/" + procesoId + "/gateways/" + creado.getId()))
                .body(creado);
    }

    @PutMapping("/{gatewayId}")
    public ResponseEntity<GatewayDto> actualizar(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId,
            @Valid @RequestBody GatewayDto dto) {
        validarAutenticacion(authentication);
        dto.setProcesoId(procesoId);
        return ResponseEntity.ok(gatewayService.actualizar(procesoId, gatewayId, dto));
    }

    @DeleteMapping("/{gatewayId}")
    public ResponseEntity<Void> eliminar(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId) {
        validarAutenticacion(authentication);
        gatewayService.eliminar(procesoId, gatewayId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{gatewayId}/reactivar")
    public ResponseEntity<GatewayDto> reactivar(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(gatewayService.reactivar(procesoId, gatewayId));
    }

    @DeleteMapping("/{gatewayId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(
            Authentication authentication,
            @PathVariable Long procesoId,
            @PathVariable Long gatewayId) {
        validarAutenticacion(authentication);
        gatewayService.eliminarFisicamente(procesoId, gatewayId);
        return ResponseEntity.noContent().build();
    }
}