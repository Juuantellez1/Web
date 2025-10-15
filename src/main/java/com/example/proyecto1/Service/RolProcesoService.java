package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.RolProcesoDto;
import com.example.proyecto1.Mapper.RolProcesoMapper;
import com.example.proyecto1.Model.Empresa;
import com.example.proyecto1.Model.RolProceso;
import com.example.proyecto1.Repository.ActividadRepository;
import com.example.proyecto1.Repository.EmpresaRepository;
import com.example.proyecto1.Repository.RolProcesoRepository;
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
public class RolProcesoService {

    private final RolProcesoRepository rolProcesoRepository;
    private final EmpresaRepository empresaRepository;
    private final ActividadRepository actividadRepository;
    private final RolProcesoMapper rolProcesoMapper;

    public List<RolProcesoDto> listarPorEmpresa(Long empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
        }
        List<RolProceso> roles = rolProcesoRepository.findAllByEmpresaId(empresaId);
        return roles.stream()
                .map(this::enrichDto)
                .collect(Collectors.toList());
    }

    public RolProcesoDto obtenerPorId(Long empresaId, Long rolId) {
        RolProceso rol = rolProcesoRepository.findByIdAndEmpresaId(rolId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol de proceso no encontrado o no pertenece a la empresa"
                ));
        return enrichDto(rol);
    }

    public RolProcesoDto crear(RolProcesoDto dto) {
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

        RolProceso rolProceso = rolProcesoMapper.toEntity(dto);
        rolProceso.setEmpresa(empresa);
        rolProceso.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Timestamp ahora = Timestamp.from(Instant.now());
        rolProceso.setFecha_registro(ahora);
        rolProceso.setFecha_modificacion(ahora);

        RolProceso guardado = rolProcesoRepository.save(rolProceso);
        return rolProcesoMapper.toDto(guardado);
    }

    public RolProcesoDto actualizar(Long empresaId, Long rolId, RolProcesoDto dto) {
        RolProceso existente = rolProcesoRepository.findByIdAndEmpresaId(rolId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol de proceso no encontrado o no pertenece a la empresa"
                ));

        rolProcesoMapper.updateEntityFromDto(dto, existente);
        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        RolProceso actualizado = rolProcesoRepository.save(existente);
        return enrichDto(actualizado);
    }

    public void eliminar(Long empresaId, Long rolId) {
        RolProceso rol = rolProcesoRepository.findByIdAndEmpresaId(rolId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol de proceso no encontrado o no pertenece a la empresa"
                ));

        if (actividadRepository.existsByRolResponsableIdAndActivoTrue(rolId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar el rol porque está asignado a actividades activas"
            );
        }

        rol.setActivo(false);
        rol.setFecha_modificacion(Timestamp.from(Instant.now()));
        rolProcesoRepository.save(rol);
    }

    public void eliminarFisicamente(Long empresaId, Long rolId) {
        if (!rolProcesoRepository.existsByIdAndEmpresaId(rolId, empresaId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Rol de proceso no encontrado o no pertenece a la empresa"
            );
        }

        if (actividadRepository.existsByRolResponsableIdAndActivoTrue(rolId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar el rol porque está asignado a actividades"
            );
        }

        rolProcesoRepository.deleteById(rolId);
    }

    private RolProcesoDto enrichDto(RolProceso rol) {
        RolProcesoDto dto = rolProcesoMapper.toDto(rol);

        List<String> procesosUtilizados = actividadRepository.findAllByRolResponsableId(rol.getId())
                .stream()
                .map(actividad -> actividad.getProceso().getNombre())
                .distinct()
                .collect(Collectors.toList());

        dto.setCantidadActividadesAsignadas((long) actividadRepository.findAllByRolResponsableId(rol.getId()).size());
        dto.setProcesosUtilizados(procesosUtilizados);

        return dto;
    }
}