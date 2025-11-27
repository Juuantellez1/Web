package com.example.proyecto1.Repository;

import com.example.proyecto1.Model.RolProceso;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias sobre RolProcesoRepository usando Mockito.
 * No usa base de datos real: solo verifica que se llamen bien los métodos.
 */
@ExtendWith(MockitoExtension.class)
class RolProcesoRepositoryTest {

    @Mock
    private RolProcesoRepository rolProcesoRepository;

    @Test
    void debeListarRolesPorEmpresa() {
        // GIVEN
        Long empresaId = 1L;
        RolProceso r1 = new RolProceso();
        RolProceso r2 = new RolProceso();
        List<RolProceso> lista = Arrays.asList(r1, r2);

        when(rolProcesoRepository.findAllByEmpresaId(empresaId))
                .thenReturn(lista);

        // WHEN
        List<RolProceso> resultado = rolProcesoRepository.findAllByEmpresaId(empresaId);

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(rolProcesoRepository).findAllByEmpresaId(empresaId);
    }

    @Test
    void debeEncontrarRolPorIdYEmpresa() {
        // GIVEN
        Long empresaId = 1L;
        Long rolId = 10L;

        RolProceso rol = new RolProceso();
        rol.setId(rolId); // Lombok @Setter

        when(rolProcesoRepository.findByIdAndEmpresaId(rolId, empresaId))
                .thenReturn(Optional.of(rol));

        // WHEN
        Optional<RolProceso> resultado =
                rolProcesoRepository.findByIdAndEmpresaId(rolId, empresaId);

        // THEN
        assertTrue(resultado.isPresent());
        assertEquals(rolId, resultado.get().getId());
        verify(rolProcesoRepository).findByIdAndEmpresaId(rolId, empresaId);
    }

    @Test
    void debeVerificarExistenciaPorIdYEmpresa() {
        // GIVEN
        Long empresaId = 1L;
        Long rolId = 10L;

        when(rolProcesoRepository.existsByIdAndEmpresaId(rolId, empresaId))
                .thenReturn(true);

        // WHEN
        boolean existe = rolProcesoRepository.existsByIdAndEmpresaId(rolId, empresaId);

        // THEN
        assertTrue(existe);
        verify(rolProcesoRepository).existsByIdAndEmpresaId(rolId, empresaId);
    }
}
