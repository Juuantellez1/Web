package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.ActividadDto;
import com.example.proyecto1.Mapper.ActividadMapper;
import com.example.proyecto1.Model.Actividad;
import com.example.proyecto1.Model.Proceso;
import com.example.proyecto1.Model.RolProceso;
import com.example.proyecto1.Repository.ActividadRepository;
import com.example.proyecto1.Repository.ProcesoRepository;
import com.example.proyecto1.Repository.RolProcesoRepository;
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
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final ProcesoRepository procesoRepository;
    private final RolProcesoRepository rolProcesoRepository;
    private final ActividadMapper actividadMapper;

    public List<ActividadDto> listarPorProceso(Long procesoId) {
        if (!procesoRepository.existsById(procesoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proceso no encontrado");
        }
        return actividadMapper.toDtoList(actividadRepository.findAllByProcesoId(procesoId));
    }

    public List<ActividadDto> listarActivasPorProceso(Long procesoId) {
        if (!procesoRepository.existsById(procesoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proceso no encontrado");
        }
        return actividadMapper.toDtoList(actividadRepository.findAllByProcesoIdAndActivoTrue(procesoId));
    }

    public ActividadDto obtenerPorId(Long procesoId, Long actividadId) {
        Actividad actividad = actividadRepository.findByIdAndProcesoId(actividadId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Actividad no encontrada o no pertenece al proceso especificado"
                ));
        return actividadMapper.toDto(actividad);
    }

    public ActividadDto crear(ActividadDto dto) {
        Proceso proceso = procesoRepository.findById(dto.getProcesoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proceso no encontrado"
                ));

        if (!proceso.getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden agregar actividades a un proceso inactivo"
            );
        }

        RolProceso rolResponsable = null;
        if (dto.getRolResponsableId() != null) {
            rolResponsable = rolProcesoRepository.findById(dto.getRolResponsableId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Rol de proceso no encontrado"
                    ));

            if (!rolResponsable.getEmpresa().getId().equals(proceso.getEmpresa().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El rol no pertenece a la misma empresa del proceso"
                );
            }

            if (!rolResponsable.getActivo()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede asignar un rol inactivo"
                );
            }
        }

        Actividad actividad = actividadMapper.toEntity(dto);
        actividad.setProceso(proceso);
        actividad.setRolResponsable(rolResponsable);
        actividad.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Timestamp ahora = Timestamp.from(Instant.now());
        actividad.setFecha_registro(ahora);
        actividad.setFecha_modificacion(ahora);

        Actividad guardada = actividadRepository.save(actividad);

        return actividadMapper.toDto(guardada);
    }

    public ActividadDto actualizar(Long procesoId, Long actividadId, ActividadDto dto) {
        Actividad existente = actividadRepository.findByIdAndProcesoId(actividadId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Actividad no encontrada o no pertenece al proceso especificado"
                ));

        if (!existente.getProceso().getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden modificar actividades de un proceso inactivo"
            );
        }

        if (dto.getRolResponsableId() != null) {
            RolProceso nuevoRol = rolProcesoRepository.findById(dto.getRolResponsableId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Rol de proceso no encontrado"
                    ));

            if (!nuevoRol.getEmpresa().getId().equals(existente.getProceso().getEmpresa().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El rol no pertenece a la misma empresa del proceso"
                );
            }

            if (!nuevoRol.getActivo()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede asignar un rol inactivo"
                );
            }

            existente.setRolResponsable(nuevoRol);
        } else if (dto.getRolResponsableId() == null && dto.getRolResponsableNombre() == null) {
            existente.setRolResponsable(null);
        }

        String nombreAnterior = existente.getNombre();
        actividadMapper.updateEntityFromDto(dto, existente);
        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Actividad actualizada = actividadRepository.save(existente);

        return actividadMapper.toDto(actualizada);
    }

    public void eliminar(Long procesoId, Long actividadId) {
        Actividad actividad = actividadRepository.findByIdAndProcesoId(actividadId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Actividad no encontrada o no pertenece al proceso especificado"
                ));

        actividad.setActivo(false);
        actividad.setFecha_modificacion(Timestamp.from(Instant.now()));
        actividadRepository.save(actividad);

    }

    public void eliminarFisicamente(Long procesoId, Long actividadId) {
        if (!actividadRepository.existsByIdAndProcesoId(actividadId, procesoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Actividad no encontrada o no pertenece al proceso especificado"
            );
        }
        actividadRepository.deleteById(actividadId);
    }

    public ActividadDto reactivar(Long procesoId, Long actividadId) {
        Actividad actividad = actividadRepository.findByIdAndProcesoId(actividadId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Actividad no encontrada"
                ));

        actividad.setActivo(true);
        actividad.setFecha_modificacion(Timestamp.from(Instant.now()));
        Actividad reactivada = actividadRepository.save(actividad);

        return actividadMapper.toDto(reactivada);
    }
}