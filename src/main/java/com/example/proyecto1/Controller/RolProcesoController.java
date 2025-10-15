package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.RolProcesoDto;
import com.example.proyecto1.Service.RolProcesoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/empresas/{empresaId}/roles-proceso")
@CrossOrigin(origins = "http://localhost:4200")
public class RolProcesoController {

    private final RolProcesoService rolProcesoService;

    @GetMapping
    public ResponseEntity<List<RolProcesoDto>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(rolProcesoService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{rolId}")
    public ResponseEntity<RolProcesoDto> obtener(
            @PathVariable Long empresaId,
            @PathVariable Long rolId) {
        return ResponseEntity.ok(rolProcesoService.obtenerPorId(empresaId, rolId));
    }

    @PostMapping
    public ResponseEntity<RolProcesoDto> crear(
            @PathVariable Long empresaId,
            @Valid @RequestBody RolProcesoDto dto) {
        dto.setEmpresaId(empresaId);
        RolProcesoDto creado = rolProcesoService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/empresas/" + empresaId + "/roles-proceso/" + creado.getId()))
                .body(creado);
    }

    @PutMapping("/{rolId}")
    public ResponseEntity<RolProcesoDto> actualizar(
            @PathVariable Long empresaId,
            @PathVariable Long rolId,
            @Valid @RequestBody RolProcesoDto dto) {
        dto.setEmpresaId(empresaId);
        return ResponseEntity.ok(rolProcesoService.actualizar(empresaId, rolId, dto));
    }

    @DeleteMapping("/{rolId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long empresaId,
            @PathVariable Long rolId) {
        rolProcesoService.eliminar(empresaId, rolId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{rolId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(
            @PathVariable Long empresaId,
            @PathVariable Long rolId) {
        rolProcesoService.eliminarFisicamente(empresaId, rolId);
        return ResponseEntity.noContent().build();
    }
}