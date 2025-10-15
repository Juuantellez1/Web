package com.example.proyecto1.Repository;

import com.example.proyecto1.Model.RolProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolProcesoRepository extends JpaRepository<RolProceso, Long> {
    List<RolProceso> findAllByEmpresaId(Long empresaId);
    Optional<RolProceso> findByIdAndEmpresaId(Long id, Long empresaId);
    boolean existsByIdAndEmpresaId(Long id, Long empresaId);
}