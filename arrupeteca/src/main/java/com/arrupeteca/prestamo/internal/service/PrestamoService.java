package com.arrupeteca.prestamo.internal.service;

import com.arrupeteca.prestamo.dto.request.PrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.PrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.PrestamoResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PrestamoService {

    Page<PrestamoResumen> obtenerTodos(
            Boolean borradoLogico,
            Long idUsuario,
            Long idEstadoPrestamo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable);

    PrestamoDetalleResponse obtenerPorId(Long id);

    PrestamoDetalleResponse crear(PrestamoRequest request);

    PrestamoDetalleResponse actualizar(Long id, PrestamoRequest request);

    void cambiarEstado(Long id, boolean estado);
}
