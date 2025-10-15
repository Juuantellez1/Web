package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.ProcesoDetalleDto;
import com.example.proyecto1.Dto.ProcesoDto;
import com.example.proyecto1.Mapper.ProcesoMapper;
import com.example.proyecto1.Model.Empresa;
import com.example.proyecto1.Model.Proceso;
import com.example.proyecto1.Model.Proceso.EstadoProceso;
import com.example.proyecto1.Repository.EmpresaRepository;
import com.example.proyecto1.Repository.ProcesoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcesoService {

    private final ProcesoRepository procesoRepository;
    private final EmpresaRepository empresaRepository;
    private final ProcesoMapper procesoMapper;

    public List<ProcesoDto> listarPorEmpresa(Long empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
        }
        List<Proceso> procesos = procesoRepository.findAllByEmpresaId(empresaId);
        return procesos.stream()
                .map(this::enrichDto)
                .collect(Collectors.toList());
    }

    public List<ProcesoDto> listarPorEmpresaYEstado(Long empresaId, EstadoProceso estado) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
        }
        return procesoRepository.findAllByEmpresaId(empresaId).stream()
                .filter(p -> p.getEstado() == estado)
                .map(this::enrichDto)
                .collect(Collectors.toList());
    }

    public List<ProcesoDto> listarPorEmpresaYCategoria(Long empresaId, String categoria) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
        }
        return procesoRepository.findAllByEmpresaId(empresaId).stream()
                .filter(p -> categoria.equalsIgnoreCase(p.getCategoria()))
                .map(this::enrichDto)
                .collect(Collectors.toList());
    }

    public ProcesoDto obtenerPorId(Long empresaId, Long procesoId) {
        Proceso proceso = procesoRepository.findByIdAndEmpresaId(procesoId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proceso no encontrado o no pertenece a la empresa"
                ));
        return enrichDto(proceso);
    }

    public ProcesoDetalleDto obtenerDetallePorId(Long empresaId, Long procesoId) {
        Proceso proceso = procesoRepository.findByIdAndEmpresaId(procesoId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proceso no encontrado o no pertenece a la empresa"
                ));
        return procesoMapper.toDetalleDto(proceso);
    }

    public ProcesoDto crear(ProcesoDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empresa no encontrada"
                ));

        if (!empresa.getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La empresa está inactiva"
            );
        }

        Proceso proceso = procesoMapper.toEntity(dto);
        proceso.setEmpresa(empresa);
        proceso.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoProceso.BORRADOR);
        proceso.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Timestamp ahora = Timestamp.from(Instant.now());
        proceso.setFecha_registro(ahora);
        proceso.setFecha_modificacion(ahora);

        Proceso guardado = procesoRepository.save(proceso);
        return procesoMapper.toDto(guardado);
    }

    public ProcesoDto actualizar(Long empresaId, Long procesoId, ProcesoDto dto) {
        Proceso existente = procesoRepository.findByIdAndEmpresaId(procesoId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proceso no encontrado o no pertenece a la empresa"
                ));

        procesoMapper.updateEntityFromDto(dto, existente);
        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Proceso actualizado = procesoRepository.save(existente);
        return enrichDto(actualizado);
    }

    public ProcesoDto cambiarEstado(Long empresaId, Long procesoId, EstadoProceso nuevoEstado) {
        Proceso proceso = procesoRepository.findByIdAndEmpresaId(procesoId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proceso no encontrado o no pertenece a la empresa"
                ));

        proceso.setEstado(nuevoEstado);
        proceso.setFecha_modificacion(Timestamp.from(Instant.now()));

        Proceso actualizado = procesoRepository.save(proceso);
        return procesoMapper.toDto(actualizado);
    }

    public void eliminar(Long empresaId, Long procesoId) {
        Proceso proceso = procesoRepository.findByIdAndEmpresaId(procesoId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proceso no encontrado o no pertenece a la empresa"
                ));

        proceso.setActivo(false);
        proceso.setFecha_modificacion(Timestamp.from(Instant.now()));
        procesoRepository.save(proceso);
    }

    public void eliminarFisicamente(Long empresaId, Long procesoId) {
        if (!procesoRepository.existsByIdAndEmpresaId(procesoId, empresaId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Proceso no encontrado o no pertenece a la empresa"
            );
        }
        procesoRepository.deleteById(procesoId);
    }

    public ProcesoDto reactivar(Long empresaId, Long procesoId) {
        Proceso proceso = procesoRepository.findByIdAndEmpresaId(procesoId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proceso no encontrado"
                ));

        proceso.setActivo(true);
        proceso.setFecha_modificacion(Timestamp.from(Instant.now()));
        Proceso reactivado = procesoRepository.save(proceso);

        return procesoMapper.toDto(reactivado);
    }

    private ProcesoDto enrichDto(Proceso proceso) {
        ProcesoDto dto = procesoMapper.toDto(proceso);
        dto.setCantidadActividades(proceso.getActividades() != null ? (long) proceso.getActividades().size() : 0L);
        dto.setCantidadArcos(proceso.getArcos() != null ? (long) proceso.getArcos().size() : 0L);
        dto.setCantidadGateways(proceso.getGateways() != null ? (long) proceso.getGateways().size() : 0L);
        return dto;
    }
}