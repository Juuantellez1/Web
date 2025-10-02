package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.CrearEmpresaRequestDto;
import com.example.proyecto1.Dto.CrearEmpresaResponseDto;
import com.example.proyecto1.Dto.EmpresaDto;
import com.example.proyecto1.Dto.UsuarioDto;
import com.example.proyecto1.Model.Empresa;
import com.example.proyecto1.Model.Rol;
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
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public List<EmpresaDto> listar() {
        return empresaRepository.findAll().stream().map(this::toDto).toList();
    }

    public EmpresaDto obtenerPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
        return toDto(empresa);
    }

    public CrearEmpresaResponseDto crearConAdmin(CrearEmpresaRequestDto request) {
        if (empresaRepository.existsByNit(request.getNit())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El NIT ya está registrado");
        }
        if (empresaRepository.existsByCorreo(request.getCorreoEmpresa())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo de la empresa ya está registrado");
        }

        Empresa empresa = new Empresa();
        empresa.setNombre(request.getNombreEmpresa());
        empresa.setNit(request.getNit());
        empresa.setCorreo(request.getCorreoEmpresa());
        empresa.setActivo(true);

        Timestamp ahora = Timestamp.from(Instant.now());
        empresa.setFecha_registro(ahora);
        empresa.setFecha_modificacion(ahora);

        Empresa empresaGuardada = empresaRepository.save(empresa);

        Usuario admin = new Usuario();
        admin.setEmpresa(empresaGuardada);
        admin.setNombre(request.getNombreAdmin());
        admin.setApellido(request.getApellidoAdmin());
        admin.setCorreo(request.getCorreoAdmin());
        admin.setPassword(passwordEncoder.encode(request.getPasswordAdmin()));
        admin.setRol(Rol.ADMIN);
        admin.setActivo(true);
        admin.setFecha_registro(ahora);
        admin.setFecha_modificacion(ahora);

        Usuario adminGuardado = usuarioRepository.save(admin);

        return CrearEmpresaResponseDto.builder()
                .empresa(toDto(empresaGuardada))
                .usuarioAdmin(toUsuarioDto(adminGuardado))
                .mensaje("Empresa y usuario administrador creados exitosamente")
                .build();
    }

    public EmpresaDto actualizar(Long id, EmpresaDto dto) {
        Empresa existente = empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));

        if (dto.getNit() != null && !dto.getNit().equalsIgnoreCase(existente.getNit())
                && empresaRepository.existsByNit(dto.getNit())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El NIT ya está registrado por otra empresa");
        }
        if (dto.getCorreo() != null && !dto.getCorreo().equalsIgnoreCase(existente.getCorreo())
                && empresaRepository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado por otra empresa");
        }

        existente.setNombre(dto.getNombre());
        existente.setNit(dto.getNit());
        existente.setCorreo(dto.getCorreo());

        if (dto.getActivo() != null) {
            existente.setActivo(dto.getActivo());
        }

        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Empresa actualizada = empresaRepository.save(existente);
        return toDto(actualizada);
    }

    public void eliminar(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
        }
        empresaRepository.deleteById(id);
    }

    private EmpresaDto toDto(Empresa e) {
        return EmpresaDto.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .nit(e.getNit())
                .correo(e.getCorreo())
                .activo(e.getActivo())
                .fecha_registro(e.getFecha_registro())
                .fecha_modificacion(e.getFecha_modificacion())
                .build();
    }

    private UsuarioDto toUsuarioDto(Usuario u) {
        return UsuarioDto.builder()
                .id(u.getId())
                .empresaId(u.getEmpresa().getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .correo(u.getCorreo())
                .rol(u.getRol())
                .activo(u.getActivo())
                .ultimo_login(u.getUltimo_login())
                .fecha_registro(u.getFecha_registro())
                .fecha_modificacion(u.getFecha_modificacion())
                .build();
    }
}