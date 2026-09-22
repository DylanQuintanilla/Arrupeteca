package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ActualizarPerfilRequest;
import com.arrupeteca.dto.request.UsuarioAdminRequest;
import com.arrupeteca.dto.response.detail.UsuarioDetalleResponse;
import com.arrupeteca.dto.response.summary.UsuarioResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.UsuarioMapper;
import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.TipoUsuarioRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.service.UsuarioService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioDetalleResponse obtenerPerfilActual(String correo) {
        log.debug("Obteniendo perfil del usuario autenticado: {}", correo);
        return usuarioRepository.findByCorreoAndBorradoLogicoFalse(correo)
                .map(usuarioMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el usuario activo con correo: " + correo));
    }

    @Override
    @Transactional
    public UsuarioDetalleResponse actualizarPerfilActual(String correo, ActualizarPerfilRequest request) {
        log.info("Actualizando perfil propio del usuario: {}", correo);
        Usuario usuario = usuarioRepository.findByCorreoAndBorradoLogicoFalse(correo)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el usuario activo con correo: " + correo));

        if (request.nuevaContrasena() != null && request.nuevaContrasena().isPresent()
                && request.nuevaContrasena().get() != null && !request.nuevaContrasena().get().isBlank()) {
            
            if (request.contrasenaActual() == null || !request.contrasenaActual().isPresent()
                    || request.contrasenaActual().get() == null || request.contrasenaActual().get().isBlank()) {
                throw new ValidacionNegocioException("Debe ingresar su contraseña actual para establecer una nueva contraseña.");
            }

            if (!passwordEncoder.matches(request.contrasenaActual().get(), usuario.getContrasena())) {
                throw new ValidacionNegocioException("La contraseña actual ingresada es incorrecta.");
            }

            usuario.setContrasena(passwordEncoder.encode(request.nuevaContrasena().get().trim()));
        }

        usuarioMapper.updatePerfil(request, usuario);
        usuario.setUltimaFechaActividad(LocalDateTime.now());
        Usuario guardado = usuarioRepository.saveAndFlush(usuario);
        log.info("Perfil del usuario ID: {} actualizado exitosamente", guardado.getId());
        return usuarioMapper.toDetalleResponse(guardado);
    }

    @Override
    public Page<UsuarioResumen> busquedaAdmin(
            Boolean borradoLogico, Boolean bloqueadoPorMora, Long idTipoUsuario,
            String termino, Pageable pageable) {
        log.debug("Búsqueda maestra de usuarios admin: borradoLogico={}, bloqueadoPorMora={}, idTipoUsuario={}, termino='{}'",
                borradoLogico, bloqueadoPorMora, idTipoUsuario, termino);
        String term = (termino != null && !termino.isBlank()) ? termino.trim() : null;
        return usuarioRepository.busqueda(borradoLogico, bloqueadoPorMora, idTipoUsuario, term, pageable);
    }

    @Override
    public UsuarioDetalleResponse busquedaAdminPorId(Long id) {
        log.debug("Buscando usuario con ID (admin): {}", id);
        return usuarioRepository.busquedaPorId(id, null)
                .map(usuarioMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Usuario con ID: " + id));
    }

    @Override
    @Transactional
    public UsuarioDetalleResponse crearAdmin(UsuarioAdminRequest request) {
        log.info("Creando usuario por administración");
        validarCreacionAdmin(request);

        String correoVal = request.correo().get().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correoVal)) {
            throw new ValidacionNegocioException("Ya existe un usuario registrado con el correo: " + correoVal);
        }

        Long idTipo = request.idTipoUsuario().get();
        TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(idTipo)
                .orElseThrow(() -> new ValidacionNegocioException("No existe el tipo de usuario con ID: " + idTipo));

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setCorreo(correoVal);
        usuario.setContrasena(passwordEncoder.encode(request.contrasena().get().trim()));
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setBorradoLogico(false);

        if (request.bloqueadoPorMora() != null && request.bloqueadoPorMora().isPresent()) {
            usuario.setBloqueadoPorMora(Boolean.TRUE.equals(request.bloqueadoPorMora().get()));
        }

        Usuario guardado = usuarioRepository.saveAndFlush(usuario);
        log.info("Usuario creado por administración exitosamente con ID: {}", guardado.getId());
        return usuarioMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    public UsuarioDetalleResponse actualizarAdmin(Long id, UsuarioAdminRequest request) {
        log.info("Actualizando usuario con ID (Admin): {}", id);
        Usuario usuarioExistente = usuarioRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Usuario con ID: " + id));

        if (request.correo() != null && request.correo().isPresent()
                && request.correo().get() != null && !request.correo().get().isBlank()) {
            String correoNuevo = request.correo().get().trim().toLowerCase();
            if (!usuarioExistente.getCorreo().equalsIgnoreCase(correoNuevo)
                    && usuarioRepository.existsByCorreo(correoNuevo)) {
                throw new ValidacionNegocioException("Ya existe un usuario registrado con el correo: " + correoNuevo);
            }
            usuarioExistente.setCorreo(correoNuevo);
        }

        if (request.contrasena() != null && request.contrasena().isPresent()
                && request.contrasena().get() != null && !request.contrasena().get().isBlank()) {
            usuarioExistente.setContrasena(passwordEncoder.encode(request.contrasena().get().trim()));
        }

        if (request.idTipoUsuario() != null && request.idTipoUsuario().isPresent()
                && request.idTipoUsuario().get() != null) {
            Long idTipo = request.idTipoUsuario().get();
            TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(idTipo)
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el tipo de usuario con ID: " + idTipo));
            usuarioExistente.setTipoUsuario(tipoUsuario);
        }

        if (request.bloqueadoPorMora() != null && request.bloqueadoPorMora().isPresent()
                && request.bloqueadoPorMora().get() != null) {
            usuarioExistente.setBloqueadoPorMora(request.bloqueadoPorMora().get());
        }

        usuarioMapper.updateEntity(request, usuarioExistente);
        Usuario guardado = usuarioRepository.saveAndFlush(usuarioExistente);
        log.info("Usuario con ID: {} actualizado exitosamente por administración", id);
        return usuarioMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado del usuario ID: {} a {}", id, !estado);
        if (usuarioRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró Usuario con ID: " + id);
        }
        if (estado) {
            usuarioRepository.activar(id);
        } else {
            usuarioRepository.desactivar(id);
        }
    }

    @Override
    @Transactional
    public void cambiarBloqueoMora(Long id, boolean bloqueado) {
        log.info("Cambiando bloqueo por mora del usuario ID: {} a {}", id, bloqueado);
        if (usuarioRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró Usuario con ID: " + id);
        }
        usuarioRepository.cambiarBloqueoMora(id, bloqueado);
    }

    private void validarCreacionAdmin(UsuarioAdminRequest request) {
        if (request.nombres() == null || !request.nombres().isPresent() || request.nombres().get() == null || request.nombres().get().isBlank()) {
            throw new ValidacionNegocioException("Los nombres son obligatorios.");
        }
        if (request.apellidos() == null || !request.apellidos().isPresent() || request.apellidos().get() == null || request.apellidos().get().isBlank()) {
            throw new ValidacionNegocioException("Los apellidos son obligatorios.");
        }
        if (request.correo() == null || !request.correo().isPresent() || request.correo().get() == null || request.correo().get().isBlank()) {
            throw new ValidacionNegocioException("El correo es obligatorio.");
        }
        if (request.contrasena() == null || !request.contrasena().isPresent() || request.contrasena().get() == null || request.contrasena().get().isBlank()) {
            throw new ValidacionNegocioException("La contraseña es obligatoria.");
        }
        if (request.idTipoUsuario() == null || !request.idTipoUsuario().isPresent() || request.idTipoUsuario().get() == null) {
            throw new ValidacionNegocioException("El tipo de usuario es obligatorio.");
        }
    }
}
