package com.arrupeteca.prestamo.dto.response.summary;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
public record PrestamoResumen(
    Long id,
    Long idSolicitud,
    Long idUsuario,
    String nombreUsuario,
    Long idEstadoPrestamo,
    String estadoPrestamo,
    LocalDateTime fechaPrestamo,
    LocalDateTime fechaLimiteDevolucion,
    Boolean borradoLogico
) {}
