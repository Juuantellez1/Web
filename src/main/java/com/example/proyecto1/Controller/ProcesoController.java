package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.ProcesoDetalleDto;
import com.example.proyecto1.Dto.ProcesoDto;
import com.example.proyecto1.Model.Proceso.EstadoProceso;
import com.example.proyecto1.Service.ProcesoService;
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
@RequestMapping("/api/empresas/{empresaId}/procesos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProcesoController {

    private final ProcesoService procesoService;

    private void validarAutenticacion(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
    }

    @GetMapping
    public ResponseEntity<List<ProcesoDto>> listarPorEmpresa(Authentication authentication, @PathVariable Long empresaId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(procesoService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProcesoDto>> listarPorEstado(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable EstadoProceso estado) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(procesoService.listarPorEmpresaYEstado(empresaId, estado));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProcesoDto>> listarPorCategoria(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable String categoria) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(procesoService.listarPorEmpresaYCategoria(empresaId, categoria));
    }

    @GetMapping("/{procesoId}")
    public ResponseEntity<ProcesoDto> obtener(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(procesoService.obtenerPorId(empresaId, procesoId));
    }

    @GetMapping("/{procesoId}/detalle")
    public ResponseEntity<ProcesoDetalleDto> obtenerDetalle(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(procesoService.obtenerDetallePorId(empresaId, procesoId));
    }

    @PostMapping
    public ResponseEntity<ProcesoDto> crear(
            Authentication authentication,
            @PathVariable Long empresaId,
            @Valid @RequestBody ProcesoDto dto) {
        validarAutenticacion(authentication);
        dto.setEmpresaId(empresaId);
        ProcesoDto creado = procesoService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/empresas/" + empresaId + "/procesos/" + creado.getId()))
                .body(creado);
    }

    @PutMapping("/{procesoId}")
    public ResponseEntity<ProcesoDto> actualizar(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long procesoId,
            @Valid @RequestBody ProcesoDto dto) {
        validarAutenticacion(authentication);
        dto.setEmpresaId(empresaId);
        return ResponseEntity.ok(procesoService.actualizar(empresaId, procesoId, dto));
    }

    @PatchMapping("/{procesoId}/estado")
    public ResponseEntity<ProcesoDto> cambiarEstado(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long procesoId,
            @RequestParam EstadoProceso estado) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(procesoService.cambiarEstado(empresaId, procesoId, estado));
    }

    @DeleteMapping("/{procesoId}")
    public ResponseEntity<Void> eliminar(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        procesoService.eliminar(empresaId, procesoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{procesoId}/reactivar")
    public ResponseEntity<ProcesoDto> reactivar(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(procesoService.reactivar(empresaId, procesoId));
    }

    @DeleteMapping("/{procesoId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(
            Authentication authentication,
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        validarAutenticacion(authentication);
        procesoService.eliminarFisicamente(empresaId, procesoId);
        return ResponseEntity.noContent().build();
    }
}