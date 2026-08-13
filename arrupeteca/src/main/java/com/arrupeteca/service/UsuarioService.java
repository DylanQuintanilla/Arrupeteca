package com.arrupeteca.service;

import com.arrupeteca.dto.request.ActualizarPerfilRequest;
import com.arrupeteca.dto.request.UsuarioAdminRequest;
import com.arrupeteca.dto.response.detail.UsuarioDetalleResponse;
import com.arrupeteca.dto.response.summary.UsuarioResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

    UsuarioDetalleResponse obtenerPerfilActual(String correo);

    UsuarioDetalleResponse actualizarPerfilActual(String correo, ActualizarPerfilRequest request);

    Page<UsuarioResumen> busquedaAdmin(
            Boolean borradoLogico, Boolean bloqueadoPorMora, Long idTipoUsuario,
            String termino, Pageable pageable);

    UsuarioDetalleResponse busquedaAdminPorId(Long id);

    UsuarioDetalleResponse crearAdmin(UsuarioAdminRequest request);

    UsuarioDetalleResponse actualizarAdmin(Long id, UsuarioAdminRequest request);

    void cambiarEstado(Long id, boolean estado);

    void cambiarBloqueoMora(Long id, boolean bloqueado);
}
