package com.arrupeteca.prestamo.dto.response.detail;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PrestamoDetalleResponse(
    Long id,
    Long idSolicitud,
    Long idUsuario,
    String nombreUsuario,
    Long idEstadoPrestamo,
    String estadoPrestamo,
    LocalDateTime fechaPrestamo,
    LocalDateTime fechaLimiteDevolucion,
    Long entregadoPorId,
    String entregadoPorNombre,
    String comentarios,
    Boolean borradoLogico,
    Long version
) {}
