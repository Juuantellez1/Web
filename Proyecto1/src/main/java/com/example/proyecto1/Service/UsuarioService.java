package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.UsuarioDto;
import com.example.proyecto1.Model.Usuario;
import com.example.proyecto1.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioDto> listar() {
        return usuarioRepository.findAll().stream().map(this::toDto).toList();
    }

    public UsuarioDto obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return toDto(usuario);
    }

    public UsuarioDto crear(UsuarioDto dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Usuario usuario = toEntity(dto);
        Timestamp ahora = Timestamp.from(Instant.now());
        usuario.setFecha_registro(ahora);
        usuario.setFecha_modificacion(ahora);
        if (usuario.getActivo() == null) {
            usuario.setActivo(Boolean.TRUE);
        }
        Usuario guardado = usuarioRepository.save(usuario);
        return toDto(guardado);
    }

    public UsuarioDto actualizar(Long id, UsuarioDto dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (dto.getCorreo() != null && !dto.getCorreo().equalsIgnoreCase(existente.getCorreo())
                && usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado por otro usuario");
        }

        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setCorreo(dto.getCorreo());
        existente.setPassword(dto.getPassword());
        existente.setActivo(dto.getActivo());
        existente.setUltimo_login(dto.getUltimo_login());
        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Usuario actualizado = usuarioRepository.save(existente);
        return toDto(actualizado);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioDto toDto(Usuario u) {
        return UsuarioDto.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .correo(u.getCorreo())
                .password(u.getPassword())
                .activo(u.getActivo())
                .ultimo_login(u.getUltimo_login())
                .fecha_registro(u.getFecha_registro())
                .fecha_modificacion(u.getFecha_modificacion())
                .build();
    }

    private Usuario toEntity(UsuarioDto dto) {
        Usuario u = new Usuario();
        u.setId(dto.getId());
        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setCorreo(dto.getCorreo());
        u.setPassword(dto.getPassword());
        u.setActivo(dto.getActivo());
        u.setUltimo_login(dto.getUltimo_login());
        u.setFecha_registro(dto.getFecha_registro());
        u.setFecha_modificacion(dto.getFecha_modificacion());
        return u;
    }
}