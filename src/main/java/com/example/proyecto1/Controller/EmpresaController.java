package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.CrearEmpresaRequestDto;
import com.example.proyecto1.Dto.CrearEmpresaResponseDto;
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
    public ResponseEntity<CrearEmpresaResponseDto> crear(@Valid @RequestBody CrearEmpresaRequestDto request) {
        CrearEmpresaResponseDto response = empresaService.crearConAdmin(request);
        return ResponseEntity
                .created(URI.create("/api/empresas/" + response.getEmpresa().getId()))
                .body(response);
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