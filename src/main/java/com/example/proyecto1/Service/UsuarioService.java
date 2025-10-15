package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.LoginDto;
import com.example.proyecto1.Dto.LoginResponseDto;
import com.example.proyecto1.Dto.UsuarioDto;
import com.example.proyecto1.Model.Empresa;
import com.example.proyecto1.Model.RolUsuario;
import com.example.proyecto1.Model.Usuario;
import com.example.proyecto1.Repository.EmpresaRepository;
import com.example.proyecto1.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final EmpresaRepository empresaRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public List<UsuarioDto> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<UsuarioDto> listarPorEmpresa(Long empresaId) {
        return usuarioRepository.findAllByEmpresaId(empresaId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public UsuarioDto obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return toDto(usuario);
    }

    public UsuarioDto obtenerPorIdYEmpresa(Long empresaId, Long id) {
        Usuario usuario = usuarioRepository.findByEmpresaIdAndId(empresaId, id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado o no pertenece a esta empresa"
                ));
        return toDto(usuario);
    }

    public UsuarioDto crear(UsuarioDto dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empresa no encontrada"
                ));

        if (!empresa.getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La empresa está inactiva"
            );
        }

        if (usuarioRepository.existsByEmpresaIdAndCorreo(dto.getEmpresaId(), dto.getCorreo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado en esta empresa"
            );
        }

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El password es obligatorio"
            );
        }

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRolUsuario() != null ? dto.getRolUsuario() : RolUsuario.LECTOR);
        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Timestamp ahora = Timestamp.from(Instant.now());
        usuario.setFecha_registro(ahora);
        usuario.setFecha_modificacion(ahora);

        Usuario guardado = usuarioRepository.save(usuario);
        return toDto(guardado);
    }

    public LoginResponseDto login(LoginDto loginDto) {
        Usuario usuario = usuarioRepository.findByCorreo(loginDto.getCorreo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas"
                ));

        if (!usuario.getEmpresa().getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La empresa está inactiva"
            );
        }

        if (!usuario.getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuario inactivo"
            );
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales inválidas"
            );
        }

        usuario.setUltimo_login(Timestamp.from(Instant.now()));
        usuarioRepository.save(usuario);

        return LoginResponseDto.builder()
                .id(usuario.getId())
                .empresaId(usuario.getEmpresa().getId())
                .nombreEmpresa(usuario.getEmpresa().getNombre())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .correo(usuario.getCorreo())
                .rolUsuario(usuario.getRol())
                .mensaje("Login exitoso")
                .exitoso(true)
                .build();
    }

    public UsuarioDto actualizar(Long id, UsuarioDto dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (dto.getCorreo() != null && !dto.getCorreo().equalsIgnoreCase(existente.getCorreo())
                && usuarioRepository.existsByEmpresaIdAndCorreo(existente.getEmpresa().getId(), dto.getCorreo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado en esta empresa"
            );
        }

        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setCorreo(dto.getCorreo());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRolUsuario() != null) {
            existente.setRol(dto.getRolUsuario());
        }

        if (dto.getActivo() != null) {
            existente.setActivo(dto.getActivo());
        }

        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Usuario actualizado = usuarioRepository.save(existente);
        return toDto(actualizado);
    }

    public UsuarioDto actualizarPorEmpresa(Long empresaId, Long id, UsuarioDto dto) {
        Usuario existente = usuarioRepository.findByEmpresaIdAndId(empresaId, id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado o no pertenece a esta empresa"
                ));

        if (dto.getCorreo() != null && !dto.getCorreo().equalsIgnoreCase(existente.getCorreo())
                && usuarioRepository.existsByEmpresaIdAndCorreo(empresaId, dto.getCorreo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado en esta empresa"
            );
        }

        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setCorreo(dto.getCorreo());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRolUsuario() != null) {
            existente.setRol(dto.getRolUsuario());
        }

        if (dto.getActivo() != null) {
            existente.setActivo(dto.getActivo());
        }

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

    public void eliminarPorEmpresa(Long empresaId, Long id) {
        Usuario usuario = usuarioRepository.findByEmpresaIdAndId(empresaId, id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado o no pertenece a esta empresa"
                ));
        usuarioRepository.delete(usuario);
    }

    public UsuarioDto cambiarRol(Long empresaId, Long id, RolUsuario nuevoRolUsuario) {
        Usuario usuario = usuarioRepository.findByEmpresaIdAndId(empresaId, id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado o no pertenece a esta empresa"
                ));

        usuario.setRol(nuevoRolUsuario);
        usuario.setFecha_modificacion(Timestamp.from(Instant.now()));

        Usuario actualizado = usuarioRepository.save(usuario);
        return toDto(actualizado);
    }

    private UsuarioDto toDto(Usuario u) {
        return UsuarioDto.builder()
                .id(u.getId())
                .empresaId(u.getEmpresa().getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .correo(u.getCorreo())
                .rolUsuario(u.getRol())
                .activo(u.getActivo())
                .ultimo_login(u.getUltimo_login())
                .fecha_registro(u.getFecha_registro())
                .fecha_modificacion(u.getFecha_modificacion())
                .build();
    }
}