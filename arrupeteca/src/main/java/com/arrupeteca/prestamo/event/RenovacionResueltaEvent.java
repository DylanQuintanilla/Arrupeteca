package com.arrupeteca.prestamo.event;

import java.time.LocalDateTime;

public record RenovacionResueltaEvent(
    Long idSolicitudRenovacion,
    Long idEjemplarPrestamo,
    Long idUsuario,
    boolean aprobada,
    LocalDateTime nuevaFechaLimite
) {}
