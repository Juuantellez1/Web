package com.example.proyecto1.Repository;

import com.example.proyecto1.Model.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GatewayRepository extends JpaRepository<Gateway, Long> {

    List<Gateway> findAllByProcesoId(Long procesoId);

    List<Gateway> findAllByProcesoIdAndActivoTrue(Long procesoId);

    Optional<Gateway> findByIdAndProcesoId(Long id, Long procesoId);

    boolean existsByIdAndProcesoId(Long id, Long procesoId);

    long countByProcesoId(Long procesoId);

    @Query("SELECT COUNT(a) FROM Arco a WHERE (a.tipoDestino = 'GATEWAY' AND a.destinoId = :gatewayId) OR (a.tipoOrigen = 'GATEWAY' AND a.origenId = :gatewayId)")
    long countArcosByGatewayId(@Param("gatewayId") Long gatewayId);
}