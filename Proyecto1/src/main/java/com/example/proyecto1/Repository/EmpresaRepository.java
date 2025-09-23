package com.example.proyecto1.Repository;

import com.example.proyecto1.Model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByNit(String nit);
    boolean existsByCorreo(String correo);
}