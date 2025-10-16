package com.example.proyecto1.Repository;

import com.example.proyecto1.Model.Arco;
import com.example.proyecto1.Model.Arco.TipoNodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArcoRepository extends JpaRepository<Arco, Long> {

    List<Arco> findAllByProcesoId(Long procesoId);

    List<Arco> findAllByProcesoIdAndActivoTrue(Long procesoId);

    Optional<Arco> findByIdAndProcesoId(Long id, Long procesoId);

    boolean existsByIdAndProcesoId(Long id, Long procesoId);

    long countByProcesoId(Long procesoId);

    List<Arco> findAllByTipoOrigenAndOrigenId(TipoNodo tipoOrigen, Long origenId);

    List<Arco> findAllByTipoDestinoAndDestinoId(TipoNodo tipoDestino, Long destinoId);

    @Query("SELECT a FROM Arco a WHERE a.proceso.id = :procesoId AND " +
            "((a.tipoOrigen = :tipo AND a.origenId = :nodoId) OR " +
            "(a.tipoDestino = :tipo AND a.destinoId = :nodoId))")
    List<Arco> findArcosByNodo(@Param("procesoId") Long procesoId,
                               @Param("tipo") TipoNodo tipo,
                               @Param("nodoId") Long nodoId);

    @Query("SELECT COUNT(a) FROM Arco a WHERE a.tipoOrigen = :tipo AND a.origenId = :nodoId AND a.activo = true")
    long countArcosSalientesByNodo(@Param("tipo") TipoNodo tipo, @Param("nodoId") Long nodoId);

    @Query("SELECT COUNT(a) FROM Arco a WHERE a.tipoDestino = :tipo AND a.destinoId = :nodoId AND a.activo = true")
    long countArcosEntrantesByNodo(@Param("tipo") TipoNodo tipo, @Param("nodoId") Long nodoId);

    boolean existsByTipoOrigenAndOrigenIdAndTipoDestinoAndDestinoIdAndProcesoId(
            TipoNodo tipoOrigen, Long origenId, TipoNodo tipoDestino, Long destinoId, Long procesoId);
}