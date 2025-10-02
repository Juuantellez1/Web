
package com.example.proyecto1.Repository;

import com.example.proyecto1.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByEmpresaIdAndCorreo(Long empresaId, String correo);
    Optional<Usuario> findByEmpresaIdAndId(Long empresaId, Long id);
    Optional<Usuario> findByEmpresaIdAndCorreo(Long empresaId, String correo);
    List<Usuario> findAllByEmpresaId(Long empresaId);
}