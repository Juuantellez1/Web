package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.ArcoDto;
import com.example.proyecto1.Mapper.ArcoMapper;
import com.example.proyecto1.Model.Actividad;
import com.example.proyecto1.Model.Arco;
import com.example.proyecto1.Model.Arco.TipoNodo;
import com.example.proyecto1.Model.Gateway;
import com.example.proyecto1.Model.Proceso;
import com.example.proyecto1.Repository.*;
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
public class ArcoService {

    private final ArcoRepository arcoRepository;
    private final ProcesoRepository procesoRepository;
    private final ActividadRepository actividadRepository;
    private final GatewayRepository gatewayRepository;
    private final ArcoMapper arcoMapper;

    public List<ArcoDto> listarPorProceso(Long procesoId) {
        if (!procesoRepository.existsById(procesoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proceso no encontrado");
        }
        return arcoRepository.findAllByProcesoId(procesoId).stream()
                .map(this::enrichDto)
                .collect(Collectors.toList());
    }

    public List<ArcoDto> listarActivosPorProceso(Long procesoId) {
        if (!procesoRepository.existsById(procesoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proceso no encontrado");
        }
        return arcoRepository.findAllByProcesoIdAndActivoTrue(procesoId).stream()
                .map(this::enrichDto)
                .collect(Collectors.toList());
    }

    public ArcoDto obtenerPorId(Long procesoId, Long arcoId) {
        Arco arco = arcoRepository.findByIdAndProcesoId(arcoId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Arco no encontrado o no pertenece al proceso especificado"
                ));
        return enrichDto(arco);
    }

    public ArcoDto crear(ArcoDto dto) {
        Proceso proceso = procesoRepository.findById(dto.getProcesoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proceso no encontrado"
                ));

        if (!proceso.getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden agregar arcos a un proceso inactivo"
            );
        }

        validarNodo(dto.getTipoOrigen(), dto.getOrigenId(), proceso.getId());
        validarNodo(dto.getTipoDestino(), dto.getDestinoId(), proceso.getId());

        if (dto.getOrigenId().equals(dto.getDestinoId()) && dto.getTipoOrigen() == dto.getTipoDestino()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Un arco no puede conectar un nodo consigo mismo"
            );
        }

        if (arcoRepository.existsByTipoOrigenAndOrigenIdAndTipoDestinoAndDestinoIdAndProcesoId(
                dto.getTipoOrigen(), dto.getOrigenId(), dto.getTipoDestino(), dto.getDestinoId(), dto.getProcesoId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un arco entre estos nodos"
            );
        }

        Arco arco = arcoMapper.toEntity(dto);
        arco.setProceso(proceso);
        arco.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Timestamp ahora = Timestamp.from(Instant.now());
        arco.setFecha_registro(ahora);
        arco.setFecha_modificacion(ahora);

        Arco guardado = arcoRepository.save(arco);
        return enrichDto(guardado);
    }

    public ArcoDto actualizar(Long procesoId, Long arcoId, ArcoDto dto) {
        Arco existente = arcoRepository.findByIdAndProcesoId(arcoId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Arco no encontrado o no pertenece al proceso especificado"
                ));

        if (!existente.getProceso().getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden modificar arcos de un proceso inactivo"
            );
        }

        if (dto.getTipoOrigen() != null && dto.getOrigenId() != null) {
            validarNodo(dto.getTipoOrigen(), dto.getOrigenId(), procesoId);
        }

        if (dto.getTipoDestino() != null && dto.getDestinoId() != null) {
            validarNodo(dto.getTipoDestino(), dto.getDestinoId(), procesoId);
        }

        Long nuevoOrigenId = dto.getOrigenId() != null ? dto.getOrigenId() : existente.getOrigenId();
        Long nuevoDestinoId = dto.getDestinoId() != null ? dto.getDestinoId() : existente.getDestinoId();
        TipoNodo nuevoTipoOrigen = dto.getTipoOrigen() != null ? dto.getTipoOrigen() : existente.getTipoOrigen();
        TipoNodo nuevoTipoDestino = dto.getTipoDestino() != null ? dto.getTipoDestino() : existente.getTipoDestino();

        if (nuevoOrigenId.equals(nuevoDestinoId) && nuevoTipoOrigen == nuevoTipoDestino) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Un arco no puede conectar un nodo consigo mismo"
            );
        }

        arcoMapper.updateEntityFromDto(dto, existente);
        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Arco actualizado = arcoRepository.save(existente);
        return enrichDto(actualizado);
    }

    public void eliminar(Long procesoId, Long arcoId) {
        Arco arco = arcoRepository.findByIdAndProcesoId(arcoId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Arco no encontrado o no pertenece al proceso especificado"
                ));

        arco.setActivo(false);
        arco.setFecha_modificacion(Timestamp.from(Instant.now()));
        arcoRepository.save(arco);
    }

    public void eliminarFisicamente(Long procesoId, Long arcoId) {
        if (!arcoRepository.existsByIdAndProcesoId(arcoId, procesoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Arco no encontrado o no pertenece al proceso especificado"
            );
        }
        arcoRepository.deleteById(arcoId);
    }

    public void eliminarArcosPorNodo(Long procesoId, TipoNodo tipoNodo, Long nodoId) {
        List<Arco> arcos = arcoRepository.findArcosByNodo(procesoId, tipoNodo, nodoId);
        Timestamp ahora = Timestamp.from(Instant.now());

        arcos.forEach(arco -> {
            arco.setActivo(false);
            arco.setFecha_modificacion(ahora);
        });

        arcoRepository.saveAll(arcos);
    }

    public ArcoDto reactivar(Long procesoId, Long arcoId) {
        Arco arco = arcoRepository.findByIdAndProcesoId(arcoId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Arco no encontrado"
                ));

        validarNodo(arco.getTipoOrigen(), arco.getOrigenId(), procesoId);
        validarNodo(arco.getTipoDestino(), arco.getDestinoId(), procesoId);

        arco.setActivo(true);
        arco.setFecha_modificacion(Timestamp.from(Instant.now()));
        Arco reactivado = arcoRepository.save(arco);

        return enrichDto(reactivado);
    }

    private void validarNodo(TipoNodo tipo, Long nodoId, Long procesoId) {
        switch (tipo) {
            case ACTIVIDAD:
                if (!actividadRepository.existsByIdAndProcesoId(nodoId, procesoId)) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Actividad no encontrada o no pertenece al proceso"
                    );
                }
                break;
            case GATEWAY:
                if (!gatewayRepository.existsByIdAndProcesoId(nodoId, procesoId)) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Gateway no encontrado o no pertenece al proceso"
                    );
                }
                break;
            case EVENTO_INICIO:
            case EVENTO_FIN:
                break;
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Tipo de nodo no válido"
                );
        }
    }

    private ArcoDto enrichDto(Arco arco) {
        ArcoDto dto = arcoMapper.toDto(arco);
        dto.setOrigenNombre(obtenerNombreNodo(arco.getTipoOrigen(), arco.getOrigenId()));
        dto.setDestinoNombre(obtenerNombreNodo(arco.getTipoDestino(), arco.getDestinoId()));
        return dto;
    }

    private String obtenerNombreNodo(TipoNodo tipo, Long nodoId) {
        switch (tipo) {
            case ACTIVIDAD:
                return actividadRepository.findById(nodoId)
                        .map(Actividad::getNombre)
                        .orElse("Actividad desconocida");
            case GATEWAY:
                return gatewayRepository.findById(nodoId)
                        .map(Gateway::getNombre)
                        .orElse("Gateway desconocido");
            case EVENTO_INICIO:
                return "Evento Inicio";
            case EVENTO_FIN:
                return "Evento Fin";
            default:
                return "Desconocido";
        }
    }
}