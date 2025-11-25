package com.example.proyecto1.Repository;  // OJO: mismo paquete que la carpeta

import com.example.proyecto1.Model.RolProceso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
public class RolProcesoRepositoryTest {  
    @Autowired
    private RolProcesoRepository rolProcesoRepository;

    @BeforeEach
    void limpiarBase() {
        rolProcesoRepository.deleteAll();
    }

    // ========== TESTS BÁSICOS DE JPA ==========
    
    @Test
    void debeGuardarYRecuperarRolProceso() {
        // Arrange
        RolProceso rol = new RolProceso();
        rol.setNombre("Administrador");
        rol.setDescripcion("Gestión completa del sistema");
        rol.setActivo(true);
        rol.setFechaCreacion(LocalDate.now());

        // Act
        RolProceso guardado = rolProcesoRepository.save(rol);
        Optional<RolProceso> recuperado = rolProcesoRepository.findById(guardado.getId());

        // Assert
        assertTrue(recuperado.isPresent());
        assertEquals("Administrador", recuperado.get().getNombre());
        assertEquals("Gestión completa del sistema", recuperado.get().getDescripcion());
    }

    @Test
    void debeGenerarIdAutomaticamente() {
        // Arrange
        RolProceso rol = crearRolProceso("Usuario", "Acceso básico");

        // Act
        RolProceso guardado = rolProcesoRepository.save(rol);

        // Assert
        assertNotNull(guardado.getId());
        assertTrue(guardado.getId() > 0);
    }

    @Test
    void debeListarTodosLosRoles() {
        // Arrange
        RolProceso rol1 = crearRolProceso("Rol1", "Descripción 1");
        RolProceso rol2 = crearRolProceso("Rol2", "Descripción 2");
        RolProceso rol3 = crearRolProceso("Rol3", "Descripción 3");
        
        rolProcesoRepository.save(rol1);
        rolProcesoRepository.save(rol2);
        rolProcesoRepository.save(rol3);

        // Act
        List<RolProceso> roles = rolProcesoRepository.findAll();

        // Assert
        assertEquals(3, roles.size());
    }

    @Test
    void debeActualizarRolProceso() {
        // Arrange
        RolProceso rol = rolProcesoRepository.save(
            crearRolProceso("Editor", "Edita contenido")
        );

        // Act
        rol.setNombre("Editor Senior");
        rol.setDescripcion("Edita y supervisa contenido");
        RolProceso actualizado = rolProcesoRepository.save(rol);

        // Assert
        assertEquals("Editor Senior", actualizado.getNombre());
        assertEquals("Edita y supervisa contenido", actualizado.getDescripcion());
        assertEquals(1, rolProcesoRepository.count()); // No duplica
    }

    @Test
    void debeEliminarRolProceso() {
        // Arrange
        RolProceso rol = rolProcesoRepository.save(
            crearRolProceso("Temporal", "Rol temporal")
        );
        Long id = rol.getId();

        // Act
        rolProcesoRepository.deleteById(id);

        // Assert
        assertFalse(rolProcesoRepository.findById(id).isPresent());
        assertEquals(0, rolProcesoRepository.count());
    }

    // ========== TESTS DE CONSULTAS PERSONALIZADAS ==========
    
    @Test
    void debeBuscarPorNombre() {
        // Arrange
        RolProceso rol = crearRolProceso("Supervisor", "Supervisa operaciones");
        rolProcesoRepository.save(rol);

        // Act
        Optional<RolProceso> resultado = rolProcesoRepository.findByNombre("Supervisor");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Supervisor", resultado.get().getNombre());
    }

    @Test
    void debeBuscarPorNombre_retornarVacio_cuandoNoExiste() {
        // Act
        Optional<RolProceso> resultado = rolProcesoRepository.findByNombre("Inexistente");

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    void debeListarRolesActivos() {
        // Arrange
        RolProceso activo1 = crearRolProceso("Activo1", "Desc1");
        activo1.setActivo(true);
        
        RolProceso inactivo = crearRolProceso("Inactivo", "Desc2");
        inactivo.setActivo(false);
        
        RolProceso activo2 = crearRolProceso("Activo2", "Desc3");
        activo2.setActivo(true);
        
        rolProcesoRepository.save(activo1);
        rolProcesoRepository.save(inactivo);
        rolProcesoRepository.save(activo2);

        // Act
        List<RolProceso> rolesActivos = rolProcesoRepository.findByActivoTrue();

        // Assert
        assertEquals(2, rolesActivos.size());
        assertTrue(rolesActivos.stream().allMatch(RolProceso::getActivo));
    }

    @Test
    void debeListarRolesInactivos() {
        // Arrange
        RolProceso activo = crearRolProceso("Activo", "Desc1");
        activo.setActivo(true);
        
        RolProceso inactivo1 = crearRolProceso("Inactivo1", "Desc2");
        inactivo1.setActivo(false);
        
        RolProceso inactivo2 = crearRolProceso("Inactivo2", "Desc3");
        inactivo2.setActivo(false);
        
        rolProcesoRepository.save(activo);
        rolProcesoRepository.save(inactivo1);
        rolProcesoRepository.save(inactivo2);

        // Act
        List<RolProceso> rolesInactivos = rolProcesoRepository.findByActivoFalse();

        // Assert
        assertEquals(2, rolesInactivos.size());
        assertTrue(rolesInactivos.stream().noneMatch(RolProceso::getActivo));
    }

    @Test
    void debeBuscarPorNombreQueContiene() {
        // Arrange
        rolProcesoRepository.save(crearRolProceso("Administrador General", "Desc1"));
        rolProcesoRepository.save(crearRolProceso("Administrador de Sistema", "Desc2"));
        rolProcesoRepository.save(crearRolProceso("Usuario Básico", "Desc3"));

        // Act
        List<RolProceso> resultado = rolProcesoRepository.findByNombreContainingIgnoreCase("admin");

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(r -> r.getNombre().toLowerCase().contains("admin")));
    }

    @Test
    void debeOrdenarPorNombreAscendente() {
        // Arrange
        rolProcesoRepository.save(crearRolProceso("Zeta", "Desc"));
        rolProcesoRepository.save(crearRolProceso("Alpha", "Desc"));
        rolProcesoRepository.save(crearRolProceso("Beta", "Desc"));

        // Act
        List<RolProceso> resultado = rolProcesoRepository.findAllByOrderByNombreAsc();

        // Assert
        assertEquals(3, resultado.size());
        assertEquals("Alpha", resultado.get(0).getNombre());
        assertEquals("Beta", resultado.get(1).getNombre());
        assertEquals("Zeta", resultado.get(2).getNombre());
    }

    @Test
    void debeBuscarPorFechaCreacion() {
        // Arrange
        LocalDate fecha = LocalDate.of(2024, 6, 15);
        
        RolProceso rol1 = crearRolProceso("Rol1", "Desc");
        rol1.setFechaCreacion(fecha);
        
        RolProceso rol2 = crearRolProceso("Rol2", "Desc");
        rol2.setFechaCreacion(LocalDate.now());
        
        rolProcesoRepository.save(rol1);
        rolProcesoRepository.save(rol2);

        // Act
        List<RolProceso> resultado = rolProcesoRepository.findByFechaCreacion(fecha);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Rol1", resultado.get(0).getNombre());
    }

    @Test
    void debeBuscarRolesCreadosDespuesDeFecha() {
        // Arrange
        LocalDate fechaBase = LocalDate.of(2024, 1, 1);
        
        RolProceso antiguo = crearRolProceso("Antiguo", "Desc");
        antiguo.setFechaCreacion(LocalDate.of(2023, 12, 1));
        
        RolProceso nuevo1 = crearRolProceso("Nuevo1", "Desc");
        nuevo1.setFechaCreacion(LocalDate.of(2024, 6, 1));
        
        RolProceso nuevo2 = crearRolProceso("Nuevo2", "Desc");
        nuevo2.setFechaCreacion(LocalDate.now());
        
        rolProcesoRepository.save(antiguo);
        rolProcesoRepository.save(nuevo1);
        rolProcesoRepository.save(nuevo2);

        // Act
        List<RolProceso> resultado = rolProcesoRepository.findByFechaCreacionAfter(fechaBase);

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(r -> r.getFechaCreacion().isAfter(fechaBase)));
    }

    @Test
    void debeContarRolesActivos() {
        // Arrange
        RolProceso activo1 = crearRolProceso("A1", "D");
        activo1.setActivo(true);
        RolProceso activo2 = crearRolProceso("A2", "D");
        activo2.setActivo(true);
        RolProceso inactivo = crearRolProceso("I", "D");
        inactivo.setActivo(false);
        
        rolProcesoRepository.save(activo1);
        rolProcesoRepository.save(activo2);
        rolProcesoRepository.save(inactivo);

        // Act
        long count = rolProcesoRepository.countByActivoTrue();

        // Assert
        assertEquals(2, count);
    }

    @Test
    void debeVerificarExistenciaPorNombre() {
        // Arrange
        rolProcesoRepository.save(crearRolProceso("Existente", "Descripción"));

        // Act & Assert
        assertTrue(rolProcesoRepository.existsByNombre("Existente"));
        assertFalse(rolProcesoRepository.existsByNombre("NoExistente"));
    }

    // ========== TESTS DE INTEGRIDAD ==========
    
    @Test
    void debeMantenerIntegridadDeDatos() {
        // Arrange
        RolProceso rol = new RolProceso();
        rol.setNombre("Test");
        rol.setDescripcion("Descripción de prueba");
        rol.setActivo(true);
        rol.setFechaCreacion(LocalDate.of(2024, 5, 20));

        // Act
        RolProceso guardado = rolProcesoRepository.save(rol);
        RolProceso recuperado = rolProcesoRepository.findById(guardado.getId()).get();

        // Assert
        assertEquals(guardado.getNombre(), recuperado.getNombre());
        assertEquals(guardado.getDescripcion(), recuperado.getDescripcion());
        assertEquals(guardado.getActivo(), recuperado.getActivo());
        assertEquals(guardado.getFechaCreacion(), recuperado.getFechaCreacion());
    }

    // ========== MÉTODOS AUXILIARES ==========
    
    private RolProceso crearRolProceso(String nombre, String descripcion) {
        RolProceso rol = new RolProceso();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);
        rol.setActivo(true);
        rol.setFechaCreacion(LocalDate.now());
        return rol;
    }
}   