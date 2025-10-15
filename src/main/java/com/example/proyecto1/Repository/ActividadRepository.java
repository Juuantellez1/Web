package com.example.proyecto1.Repository;

import com.example.proyecto1.Model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    List<Actividad> findAllByProcesoId(Long procesoId);

    List<Actividad> findAllByProcesoIdAndActivoTrue(Long procesoId);

    Optional<Actividad> findByIdAndProcesoId(Long id, Long procesoId);

    boolean existsByIdAndProcesoId(Long id, Long procesoId);

    long countByProcesoId(Long procesoId);

    List<Actividad> findAllByRolResponsableId(Long rolResponsableId);

    @Query("SELECT COUNT(a) > 0 FROM Actividad a WHERE a.rolResponsable.id = :rolId AND a.activo = true")
    boolean existsByRolResponsableIdAndActivoTrue(@Param("rolId") Long rolId);
}