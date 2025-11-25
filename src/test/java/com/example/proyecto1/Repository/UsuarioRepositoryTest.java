package com.example.proyecto1.Repository;

import com.example.proyecto1.Model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias sobre el repositorio de Usuario usando Mockito.
 * NO usa base de datos real, solo verifica que se llamen bien los métodos.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void debeEncontrarUsuarioPorCorreo() {
        // GIVEN
        String correo = "test@test.com";
        Usuario usuario = construirUsuario(1L, correo);

        when(usuarioRepository.findByCorreo(correo))
                .thenReturn(Optional.of(usuario));

        // WHEN
        Optional<Usuario> resultado = usuarioRepository.findByCorreo(correo);

        // THEN
        assertTrue(resultado.isPresent());
        assertEquals(correo, resultado.get().getCorreo());
        verify(usuarioRepository).findByCorreo(correo);
    }

    @Test
    void debeListarUsuariosPorEmpresa() {
        // GIVEN
        Long empresaId = 10L;
        Usuario u1 = construirUsuario(1L, "u1@test.com");
        Usuario u2 = construirUsuario(2L, "u2@test.com");

        when(usuarioRepository.findAllByEmpresaId(empresaId))
                .thenReturn(Arrays.asList(u1, u2));

        // WHEN
        List<Usuario> resultado = usuarioRepository.findAllByEmpresaId(empresaId);

        // THEN
        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAllByEmpresaId(empresaId);
    }

    @Test
    void debeVerificarExistenciaPorEmpresaYCorreo() {
        // GIVEN
        Long empresaId = 5L;
        String correo = "existe@test.com";

        when(usuarioRepository.existsByEmpresaIdAndCorreo(empresaId, correo))
                .thenReturn(true);

        // WHEN
        boolean existe = usuarioRepository.existsByEmpresaIdAndCorreo(empresaId, correo);

        // THEN
        assertTrue(existe);
        verify(usuarioRepository).existsByEmpresaIdAndCorreo(empresaId, correo);
    }

    // --- método de apoyo para crear un usuario en memoria ---
    private Usuario construirUsuario(Long id, String correo) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setCorreo(correo);
        u.setNombre("Nombre");
        u.setApellido("Apellido");
        u.setPassword("pass");
        u.setActivo(true);

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        u.setFecha_registro(ahora);
        u.setFecha_modificacion(ahora);


        return u;
    }
}
