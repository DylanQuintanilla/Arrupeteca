package com.arrupeteca.prestamo.event;

import java.time.LocalDateTime;
import java.util.List;

public record PrestamoRealizadoEvent(
    Long idPrestamo,
    Long idSolicitud,
    Long idUsuario,
    List<Long> idsEjemplares,
    LocalDateTime fechaPrestamo,
    LocalDateTime fechaLimiteDevolucion
) {}
