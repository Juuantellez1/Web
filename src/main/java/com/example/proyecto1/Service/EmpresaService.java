
package com.example.proyecto1.Service;

import com.example.proyecto1.Dto.CrearEmpresaRequestDto;
import com.example.proyecto1.Dto.CrearEmpresaResponseDto;
import com.example.proyecto1.Dto.EmpresaDto;
import com.example.proyecto1.Dto.UsuarioDto;
import com.example.proyecto1.Mapper.EmpresaMapper;
import com.example.proyecto1.Mapper.UsuarioMapper;
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
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmpresaMapper empresaMapper;
    private final UsuarioMapper usuarioMapper;

    public List<EmpresaDto> listar() {
        return empresaMapper.toDtoList(empresaRepository.findAll());
    }

    public EmpresaDto obtenerPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
        return empresaMapper.toDto(empresa);
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
        admin.setRol(RolUsuario.ADMIN);
        admin.setActivo(true);
        admin.setFecha_registro(ahora);
        admin.setFecha_modificacion(ahora);

        Usuario adminGuardado = usuarioRepository.save(admin);

        return CrearEmpresaResponseDto.builder()
                .empresa(empresaMapper.toDto(empresaGuardada))
                .usuarioAdmin(usuarioMapper.toDto(adminGuardado))
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

        empresaMapper.updateEntityFromDto(dto, existente);
        existente.setFecha_modificacion(Timestamp.from(Instant.now()));

        Empresa actualizada = empresaRepository.save(existente);
        return empresaMapper.toDto(actualizada);
    }

    public void eliminar(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
        }
        empresaRepository.deleteById(id);
    }
}