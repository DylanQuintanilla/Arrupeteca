package com.arrupeteca.transferencia.internal.service;

import com.arrupeteca.transferencia.dto.request.SolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.SolicitudTransferenciaDetalleResponse;
import com.arrupeteca.transferencia.dto.response.SolicitudTransferenciaResumenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SolicitudTransferenciaService {

    Page<SolicitudTransferenciaResumenResponse> busquedaGlobal(
            Long idUsuario, Long idSalonDestino, Long idEstadoSolicitud, String motivo, Pageable pageable);

    Page<SolicitudTransferenciaResumenResponse> busquedaAdmin(
            Long idUsuario, Long idSalonDestino, Long idEstadoSolicitud, String motivo, Boolean borradoLogico, Pageable pageable);

    SolicitudTransferenciaDetalleResponse busquedaGlobalPorId(Long id);

    SolicitudTransferenciaDetalleResponse busquedaAdminPorId(Long id);

    SolicitudTransferenciaDetalleResponse crear(SolicitudTransferenciaRequest request);

    SolicitudTransferenciaDetalleResponse actualizar(Long id, SolicitudTransferenciaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
