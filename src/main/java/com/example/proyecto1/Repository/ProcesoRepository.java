package com.example.proyecto1.Repository;

import com.example.proyecto1.Model.Proceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcesoRepository extends JpaRepository<Proceso, Long> {
    List<Proceso> findAllByEmpresaId(Long empresaId);
    Optional<Proceso> findByIdAndEmpresaId(Long id, Long empresaId);
    boolean existsByIdAndEmpresaId(Long id, Long empresaId);
}