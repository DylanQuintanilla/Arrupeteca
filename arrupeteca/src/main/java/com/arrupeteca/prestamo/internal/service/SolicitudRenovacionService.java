package com.arrupeteca.prestamo.internal.service;

import com.arrupeteca.prestamo.dto.request.SolicitudRenovacionRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudRenovacionDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudRenovacionResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SolicitudRenovacionService {

    Page<SolicitudRenovacionResumen> obtenerTodos(
            Boolean borradoLogico,
            Long idUsuario,
            Long idEstadoSolicitud,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable);

    SolicitudRenovacionDetalleResponse obtenerPorId(Long id);

    SolicitudRenovacionDetalleResponse solicitarRenovacion(SolicitudRenovacionRequest request);

    SolicitudRenovacionDetalleResponse responderRenovacion(Long id, SolicitudRenovacionRequest request);

    void cambiarEstado(Long id, boolean estado);
}
