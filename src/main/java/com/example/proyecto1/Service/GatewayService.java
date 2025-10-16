package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.GatewayDto;
import com.example.proyecto1.Mapper.GatewayMapper;
import com.example.proyecto1.Model.Gateway;
import com.example.proyecto1.Model.Proceso;
import com.example.proyecto1.Repository.GatewayRepository;
import com.example.proyecto1.Repository.ProcesoRepository;
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
public class GatewayService {

    private final GatewayRepository gatewayRepository;
    private final ProcesoRepository procesoRepository;
    private final GatewayMapper gatewayMapper;

    public List<GatewayDto> listarPorProceso(Long procesoId) {
        if (!procesoRepository.existsById(procesoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proceso no encontrado");
        }
        return gatewayMapper.toDtoList(gatewayRepository.findAllByProcesoId(procesoId));
    }

    public List<GatewayDto> listarActivosPorProceso(Long procesoId) {
        if (!procesoRepository.existsById(procesoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proceso no encontrado");
        }
        return gatewayMapper.toDtoList(gatewayRepository.findAllByProcesoIdAndActivoTrue(procesoId));
    }

    public GatewayDto obtenerPorId(Long procesoId, Long gatewayId) {
        Gateway gateway = gatewayRepository.findByIdAndProcesoId(gatewayId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Gateway no encontrado o no pertenece al proceso especificado"
                ));
        return enrichDto(gateway);
    }

    public GatewayDto crear(GatewayDto dto) {
        Proceso proceso = procesoRepository.findById(dto.getProcesoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proceso no encontrado"
                ));

        if (!proceso.getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden agregar gateways a un proceso inactivo"
            );
        }

        validarTipoGateway(dto.getTipo());

        Gateway gateway = gatewayMapper.toEntity(dto);
        gateway.setProceso(proceso);
        gateway.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Timestamp ahora = Timestamp.from(Instant.now());
        gateway.setFecha_registro(ahora);
        gateway.setFecha_modificacion(ahora);

        Gateway guardado = gatewayRepository.save(gateway);
        return gatewayMapper.toDto(guardado);
    }

    public GatewayDto actualizar(Long procesoId, Long gatewayId, GatewayDto dto) {
        Gateway existente = gatewayRepository.findByIdAndProcesoId(gatewayId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Gateway no encontrado o no pertenece al proceso especificado"
                ));

        if (!existente.getProceso().getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden modificar gateways de un proceso inactivo"
            );
        }

        if (dto.getTipo() != null) {
            validarTipoGateway(dto.getTipo());
        }

        gatewayMapper.updateEntityFromDto(dto, existente);
        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Gateway actualizado = gatewayRepository.save(existente);
        return enrichDto(actualizado);
    }

    public void eliminar(Long procesoId, Long gatewayId) {
        Gateway gateway = gatewayRepository.findByIdAndProcesoId(gatewayId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Gateway no encontrado o no pertenece al proceso especificado"
                ));

        long arcosConectados = gatewayRepository.countArcosByGatewayId(gatewayId);
        if (arcosConectados > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar el gateway porque tiene arcos conectados. Total: " + arcosConectados
            );
        }

        gateway.setActivo(false);
        gateway.setFecha_modificacion(Timestamp.from(Instant.now()));
        gatewayRepository.save(gateway);
    }

    public void eliminarFisicamente(Long procesoId, Long gatewayId) {
        if (!gatewayRepository.existsByIdAndProcesoId(gatewayId, procesoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Gateway no encontrado o no pertenece al proceso especificado"
            );
        }

        long arcosConectados = gatewayRepository.countArcosByGatewayId(gatewayId);
        if (arcosConectados > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar físicamente el gateway porque tiene arcos conectados"
            );
        }

        gatewayRepository.deleteById(gatewayId);
    }

    public GatewayDto reactivar(Long procesoId, Long gatewayId) {
        Gateway gateway = gatewayRepository.findByIdAndProcesoId(gatewayId, procesoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Gateway no encontrado"
                ));

        gateway.setActivo(true);
        gateway.setFecha_modificacion(Timestamp.from(Instant.now()));
        Gateway reactivado = gatewayRepository.save(gateway);

        return gatewayMapper.toDto(reactivado);
    }

    private void validarTipoGateway(Gateway.TipoGateway tipo) {
        if (tipo == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de gateway es obligatorio"
            );
        }
    }

    private GatewayDto enrichDto(Gateway gateway) {
        GatewayDto dto = gatewayMapper.toDto(gateway);
        long arcosConectados = gatewayRepository.countArcosByGatewayId(gateway.getId());
        dto.setCantidadArcosEntrantes(arcosConectados);
        dto.setCantidadArcosSalientes(arcosConectados);
        return dto;
    }
}