package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.EmpresaDto;
import com.example.proyecto1.Model.Empresa;
import com.example.proyecto1.Repository.EmpresaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;



    public List<EmpresaDto> listar() {
        return empresaRepository.findAll().stream().map(this::toDto).toList();
    }

    public EmpresaDto obtenerPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
        return toDto(empresa);
    }

    public EmpresaDto crear(EmpresaDto dto) {
        if (empresaRepository.existsByNit(dto.getNit())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El NIT ya está registrado");
        }
        if (empresaRepository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        Empresa empresa = toEntity(dto);
        Timestamp ahora = Timestamp.from(Instant.now());
        empresa.setFecha_registro(ahora);
        empresa.setFecha_modificacion(ahora);

        Empresa guardada = empresaRepository.save(empresa);
        return toDto(guardada);
    }

    public EmpresaDto actualizar(Long id, EmpresaDto dto) {
        Empresa existente = empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));

        if (dto.getNit() != null && !dto.getNit().equalsIgnoreCase(existente.getNit())
                && empresaRepository.existsByNit(dto.getNit())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El NIT ya está registrado por otra empresa");
        }
        if (dto.getCorreo() != null && !dto.getCorreo().equalsIgnoreCase(existente.getCorreo())
                && empresaRepository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado por otra empresa");
        }

        existente.setNombre(dto.getNombre());
        existente.setNit(dto.getNit());
        existente.setCorreo(dto.getCorreo());
        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Empresa actualizada = empresaRepository.save(existente);
        return toDto(actualizada);
    }

    public void eliminar(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
        }
        empresaRepository.deleteById(id);
    }

    private EmpresaDto toDto(Empresa e) {
        return EmpresaDto.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .nit(e.getNit())
                .correo(e.getCorreo())
                .fecha_registro(e.getFecha_registro())
                .fecha_modificacion(e.getFecha_modificacion())
                .build();
    }

    private Empresa toEntity(EmpresaDto dto) {
        Empresa e = new Empresa();
        e.setId(dto.getId());
        e.setNombre(dto.getNombre());
        e.setNit(dto.getNit());
        e.setCorreo(dto.getCorreo());
        e.setFecha_registro(dto.getFecha_registro());
        e.setFecha_modificacion(dto.getFecha_modificacion());
        return e;
    }
}