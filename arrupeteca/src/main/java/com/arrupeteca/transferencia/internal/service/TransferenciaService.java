package com.arrupeteca.transferencia.internal.service;

import com.arrupeteca.transferencia.dto.request.TransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.TransferenciaDetalleResponse;
import com.arrupeteca.transferencia.dto.response.TransferenciaResumenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferenciaService {

    Page<TransferenciaResumenResponse> busquedaGlobal(
            Long idUsuarioEnvia, Long idUsuarioRecibe, Long idMuebleOrigen, Long idMuebleDestino, Long idEstadoTransferencia, Boolean confirmada, Pageable pageable);

    Page<TransferenciaResumenResponse> busquedaAdmin(
            Long idUsuarioEnvia, Long idUsuarioRecibe, Long idMuebleOrigen, Long idMuebleDestino, Long idEstadoTransferencia, Boolean confirmada, Boolean borradoLogico, Pageable pageable);

    TransferenciaDetalleResponse busquedaGlobalPorId(Long id);

    TransferenciaDetalleResponse busquedaAdminPorId(Long id);

    TransferenciaDetalleResponse crear(TransferenciaRequest request);

    TransferenciaDetalleResponse actualizar(Long id, TransferenciaRequest request);

    TransferenciaDetalleResponse confirmarRecepcion(Long id, Long idUsuarioRecibe, String observacionRecepcion);

    void cambiarEstado(Long id, boolean estado);
}
