package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.ProcesoDetalleDto;
import com.example.proyecto1.Dto.ProcesoDto;
import com.example.proyecto1.Model.Proceso.EstadoProceso;
import com.example.proyecto1.Service.ProcesoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/empresas/{empresaId}/procesos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProcesoController {

    private final ProcesoService procesoService;

    @GetMapping
    public ResponseEntity<List<ProcesoDto>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(procesoService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ProcesoDto>> listarPorEstado(
            @PathVariable Long empresaId,
            @PathVariable EstadoProceso estado) {
        return ResponseEntity.ok(procesoService.listarPorEmpresaYEstado(empresaId, estado));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProcesoDto>> listarPorCategoria(
            @PathVariable Long empresaId,
            @PathVariable String categoria) {
        return ResponseEntity.ok(procesoService.listarPorEmpresaYCategoria(empresaId, categoria));
    }

    @GetMapping("/{procesoId}")
    public ResponseEntity<ProcesoDto> obtener(
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        return ResponseEntity.ok(procesoService.obtenerPorId(empresaId, procesoId));
    }

    @GetMapping("/{procesoId}/detalle")
    public ResponseEntity<ProcesoDetalleDto> obtenerDetalle(
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        return ResponseEntity.ok(procesoService.obtenerDetallePorId(empresaId, procesoId));
    }

    @PostMapping
    public ResponseEntity<ProcesoDto> crear(
            @PathVariable Long empresaId,
            @Valid @RequestBody ProcesoDto dto) {
        dto.setEmpresaId(empresaId);
        ProcesoDto creado = procesoService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/empresas/" + empresaId + "/procesos/" + creado.getId()))
                .body(creado);
    }

    @PutMapping("/{procesoId}")
    public ResponseEntity<ProcesoDto> actualizar(
            @PathVariable Long empresaId,
            @PathVariable Long procesoId,
            @Valid @RequestBody ProcesoDto dto) {
        dto.setEmpresaId(empresaId);
        return ResponseEntity.ok(procesoService.actualizar(empresaId, procesoId, dto));
    }

    @PatchMapping("/{procesoId}/estado")
    public ResponseEntity<ProcesoDto> cambiarEstado(
            @PathVariable Long empresaId,
            @PathVariable Long procesoId,
            @RequestParam EstadoProceso estado) {
        return ResponseEntity.ok(procesoService.cambiarEstado(empresaId, procesoId, estado));
    }

    @DeleteMapping("/{procesoId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        procesoService.eliminar(empresaId, procesoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{procesoId}/reactivar")
    public ResponseEntity<ProcesoDto> reactivar(
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        return ResponseEntity.ok(procesoService.reactivar(empresaId, procesoId));
    }

    @DeleteMapping("/{procesoId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(
            @PathVariable Long empresaId,
            @PathVariable Long procesoId) {
        procesoService.eliminarFisicamente(empresaId, procesoId);
        return ResponseEntity.noContent().build();
    }
}