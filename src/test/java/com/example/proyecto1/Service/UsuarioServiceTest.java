package com.example.proyecto1.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.proyecto1.Dto.UsuarioDto;
import com.example.proyecto1.Mapper.UsuarioMapper;
import com.example.proyecto1.Model.Usuario;
import com.example.proyecto1.Repository.EmpresaRepository;
import com.example.proyecto1.Repository.UsuarioRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void listar_debeRetornarListaUsuarioDto() {
        // GIVEN
      Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        List<Usuario> entidades = Arrays.asList(u1, u2);

        when(usuarioRepository.findAll()).thenReturn(entidades);

        UsuarioDto dto1 = new UsuarioDto();
        UsuarioDto dto2 = new UsuarioDto();
        List<UsuarioDto> dtos = Arrays.asList(dto1, dto2);

        when(usuarioMapper.toDtoList(entidades)).thenReturn(dtos);

        // WHEN
        List<UsuarioDto> resultado = usuarioService.listar();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAll();
        verify(usuarioMapper).toDtoList(entidades);
    }
}
