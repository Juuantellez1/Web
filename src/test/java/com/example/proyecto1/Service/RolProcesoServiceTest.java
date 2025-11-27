package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.RolProcesoDto;
import com.example.proyecto1.Mapper.RolProcesoMapper;
import com.example.proyecto1.Model.Actividad;
import com.example.proyecto1.Model.Proceso;
import com.example.proyecto1.Model.RolProceso;
import com.example.proyecto1.Repository.ActividadRepository;
import com.example.proyecto1.Repository.EmpresaRepository;
import com.example.proyecto1.Repository.RolProcesoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolProcesoServiceTest {

    @Mock
    private RolProcesoRepository rolProcesoRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private ActividadRepository actividadRepository;

    @Mock
    private RolProcesoMapper rolProcesoMapper;

    @InjectMocks
    private RolProcesoService rolProcesoService;

    @Test
    void listarPorEmpresa_cuandoEmpresaNoExiste_debeLanzar404() {
        // GIVEN
        Long empresaId = 1L;
        when(empresaRepository.existsById(empresaId)).thenReturn(false);

        // WHEN + THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> rolProcesoService.listarPorEmpresa(empresaId)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Empresa no encontrada", ex.getReason());

        verify(empresaRepository).existsById(empresaId);
        verifyNoInteractions(rolProcesoRepository, actividadRepository, rolProcesoMapper);
    }

    @Test
    void listarPorEmpresa_cuandoEmpresaExiste_debeRetornarListaRolProcesoDto() {
        // GIVEN
        Long empresaId = 1L;
        when(empresaRepository.existsById(empresaId)).thenReturn(true);

        // Dos roles de ejemplo
        RolProceso rol1 = new RolProceso();
        rol1.setId(10L);
        RolProceso rol2 = new RolProceso();
        rol2.setId(20L);
        List<RolProceso> roles = Arrays.asList(rol1, rol2);

        when(rolProcesoRepository.findAllByEmpresaId(empresaId)).thenReturn(roles);

        // Actividades asociadas a cada rol para que enrichDto no reviente
        Proceso procesoA = new Proceso();
        procesoA.setNombre("Proceso A");
        Actividad actA = new Actividad();
        actA.setProceso(procesoA);

        Proceso procesoB = new Proceso();
        procesoB.setNombre("Proceso B");
        Actividad actB1 = new Actividad();
        actB1.setProceso(procesoB);

        // IMPORTANTE: enrichDto llama DOS VECES a findAllByRolResponsableId por rol
        when(actividadRepository.findAllByRolResponsableId(10L))
                .thenReturn(Collections.singletonList(actA));
        when(actividadRepository.findAllByRolResponsableId(20L))
                .thenReturn(Arrays.asList(actB1, actB1));

        // Mapper de entidad a DTO
        RolProcesoDto dto1 = new RolProcesoDto();
        dto1.setId(10L);
        RolProcesoDto dto2 = new RolProcesoDto();
        dto2.setId(20L);

        when(rolProcesoMapper.toDto(rol1)).thenReturn(dto1);
        when(rolProcesoMapper.toDto(rol2)).thenReturn(dto2);

        // WHEN
        List<RolProcesoDto> resultado = rolProcesoService.listarPorEmpresa(empresaId);

            // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(10L, resultado.get(0).getId());
        assertEquals(20L, resultado.get(1).getId());

        verify(empresaRepository).existsById(empresaId);
        verify(rolProcesoRepository).findAllByEmpresaId(empresaId);
        verify(rolProcesoMapper).toDto(rol1);
        verify(rolProcesoMapper).toDto(rol2);

    }
}
