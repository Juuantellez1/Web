package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.*;
import com.example.proyecto1.Mapper.EmpresaMapper;
import com.example.proyecto1.Mapper.UsuarioMapper;
import com.example.proyecto1.Model.Empresa;
import com.example.proyecto1.Model.RolUsuario;
import com.example.proyecto1.Model.Usuario;
import com.example.proyecto1.Repository.EmpresaRepository;
import com.example.proyecto1.Repository.UsuarioRepository;
import com.example.proyecto1.Security.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final EmpresaRepository empresaRepository;
    private final UsuarioMapper usuarioMapper;
    private final EmpresaMapper empresaMapper;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public List<UsuarioDto> listar() {
        return usuarioMapper.toDtoList(usuarioRepository.findAll());
    }

    public List<UsuarioDto> listarPorEmpresa(Long empresaId) {
        return usuarioMapper.toDtoList(usuarioRepository.findAllByEmpresaId(empresaId));
    }

    public UsuarioDto obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return usuarioMapper.toDto(usuario);
    }

    public UsuarioDto obtenerPorIdYEmpresa(Long empresaId, Long id) {
        Usuario usuario = usuarioRepository.findByEmpresaIdAndId(empresaId, id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado o no pertenece a esta empresa"
                ));
        return usuarioMapper.toDto(usuario);
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

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setEmpresa(empresa);

        String hashedPassword = jwtUtil.hashSHA1(dto.getPassword());
        usuario.setPassword(hashedPassword);

        usuario.setRol(dto.getRolUsuario() != null ? dto.getRolUsuario() : RolUsuario.LECTOR);
        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Timestamp ahora = Timestamp.from(Instant.now());
        usuario.setFecha_registro(ahora);
        usuario.setFecha_modificacion(ahora);

        Usuario guardado = usuarioRepository.save(usuario);
        return usuarioMapper.toDto(guardado);
    }

    public AuthorizedDTO login(LoginDto loginDto) {
        String passwordHash = jwtUtil.hashSHA1(loginDto.getPassword());

        Usuario usuario = usuarioRepository.findByCorreo(loginDto.getCorreo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no existe"));

        System.out.println("================ DEBUG LOGIN ================");
        System.out.println("Correo: " + loginDto.getCorreo());
        System.out.println("Pass Ingresada (Sin Hash): " + loginDto.getPassword());
        System.out.println("Pass Ingresada (Hash SHA1): " + passwordHash);
        System.out.println("Pass en Base de Datos:      " + usuario.getPassword());
        System.out.println("¿Coinciden?: " + passwordHash.equals(usuario.getPassword()));
        System.out.println("=============================================");

        if (!passwordHash.equals(usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        if (!usuario.getEmpresa().getActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La empresa está inactiva");
        }
        if (!usuario.getActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario inactivo");
        }

        usuario.setUltimo_login(Timestamp.from(Instant.now()));
        usuarioRepository.save(usuario);

        UserExtendDTO userExtendDTO = convertirAUserExtendDTO(usuario);

        try {
            String userJson = objectMapper.writeValueAsString(userExtendDTO);
            String token = jwtUtil.generateToken(userJson, usuario.getRol().name());

            return new AuthorizedDTO(userExtendDTO, token, "Bearer");
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar token");
        }
    }

    public UsuarioDto actualizar(Long id, UsuarioDto dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (dto.getCorreo() != null && !dto.getCorreo().equalsIgnoreCase(existente.getCorreo())
                && usuarioRepository.existsByEmpresaIdAndCorreo(existente.getEmpresa().getId(), dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado en esta empresa");
        }

        usuarioMapper.updateEntityFromDto(dto, existente);

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existente.setPassword(jwtUtil.hashSHA1(dto.getPassword()));
        }

        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Usuario actualizado = usuarioRepository.save(existente);
        return usuarioMapper.toDto(actualizado);
    }

    public UsuarioDto actualizarPorEmpresa(Long empresaId, Long id, UsuarioDto dto) {
        Usuario existente = usuarioRepository.findByEmpresaIdAndId(empresaId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado o no pertenece a esta empresa"));

        if (dto.getCorreo() != null && !dto.getCorreo().equalsIgnoreCase(existente.getCorreo())
                && usuarioRepository.existsByEmpresaIdAndCorreo(empresaId, dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado en esta empresa");
        }

        usuarioMapper.updateEntityFromDto(dto, existente);

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existente.setPassword(jwtUtil.hashSHA1(dto.getPassword()));
        }

        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Usuario actualizado = usuarioRepository.save(existente);
        return usuarioMapper.toDto(actualizado);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public void eliminarPorEmpresa(Long empresaId, Long id) {
        Usuario usuario = usuarioRepository.findByEmpresaIdAndId(empresaId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado o no pertenece a esta empresa"));
        usuarioRepository.delete(usuario);
    }

    public UsuarioDto cambiarRol(Long empresaId, Long id, RolUsuario nuevoRolUsuario) {
        Usuario usuario = usuarioRepository.findByEmpresaIdAndId(empresaId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado o no pertenece a esta empresa"));

        usuario.setRol(nuevoRolUsuario);
        usuario.setFecha_modificacion(Timestamp.from(Instant.now()));

        Usuario actualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toDto(actualizado);
    }

    private UserExtendDTO convertirAUserExtendDTO(Usuario usuario) {
        UsuarioDto base = usuarioMapper.toDto(usuario);
        UserExtendDTO extend = new UserExtendDTO();
        extend.setId(base.getId());
        extend.setEmpresaId(base.getEmpresaId());
        extend.setNombre(base.getNombre());
        extend.setApellido(base.getApellido());
        extend.setCorreo(base.getCorreo());
        extend.setRolUsuario(base.getRolUsuario());
        extend.setActivo(base.getActivo());
        extend.setUltimo_login(base.getUltimo_login());
        extend.setFecha_registro(base.getFecha_registro());
        extend.setFecha_modificacion(base.getFecha_modificacion());
        extend.setEnterprise(empresaMapper.toDto(usuario.getEmpresa()));
        return extend;
    }
}