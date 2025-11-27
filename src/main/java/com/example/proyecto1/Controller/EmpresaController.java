package com.example.proyecto1.Controller;

import com.example.proyecto1.Dto.CrearEmpresaRequestDto;
import com.example.proyecto1.Dto.CrearEmpresaResponseDto;
import com.example.proyecto1.Dto.EmpresaDto;
import com.example.proyecto1.Service.EmpresaService;
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
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpresaController {

    private final EmpresaService empresaService;

    private void validarAutenticacion(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
    }

    @GetMapping
    public ResponseEntity<List<EmpresaDto>> listar(Authentication authentication) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(empresaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDto> obtener(Authentication authentication, @PathVariable Long id) {
        validarAutenticacion(authentication);
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
    public ResponseEntity<EmpresaDto> actualizar(Authentication authentication, @PathVariable Long id, @Valid @RequestBody EmpresaDto dto) {
        validarAutenticacion(authentication);
        return ResponseEntity.ok(empresaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication authentication, @PathVariable Long id) {
        validarAutenticacion(authentication);
        empresaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}