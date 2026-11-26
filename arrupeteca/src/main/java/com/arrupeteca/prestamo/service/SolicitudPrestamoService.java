package com.arrupeteca.prestamo.service;

import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudPrestamoResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SolicitudPrestamoService {

    Page<SolicitudPrestamoResumen> obtenerTodos(
            Boolean borradoLogico,
            Long idUsuario,
            Long idEstadoSolicitud,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable);

    SolicitudPrestamoDetalleResponse obtenerPorId(Long id);

    SolicitudPrestamoDetalleResponse crear(SolicitudPrestamoRequest request);

    SolicitudPrestamoDetalleResponse actualizar(Long id, SolicitudPrestamoRequest request);

    void cambiarEstado(Long id, boolean estado);
}
