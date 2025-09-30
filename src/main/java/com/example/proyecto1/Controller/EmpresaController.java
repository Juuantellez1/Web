package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.EmpresaDto;
import com.example.proyecto1.Service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<EmpresaDto>> listar() {
        return ResponseEntity.ok(empresaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaDto> crear(@Valid @RequestBody EmpresaDto dto) {
        EmpresaDto creada = empresaService.crear(dto);
        return ResponseEntity.created(URI.create("/api/empresas/" + creada.getId())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDto> actualizar(@PathVariable Long id, @Valid @RequestBody EmpresaDto dto) {
        return ResponseEntity.ok(empresaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empresaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}