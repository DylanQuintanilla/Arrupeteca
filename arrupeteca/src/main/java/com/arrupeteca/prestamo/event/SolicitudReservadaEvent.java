package com.arrupeteca.prestamo.event;

import java.time.LocalDateTime;
import java.util.List;

public record SolicitudReservadaEvent(
    Long idSolicitud,
    Long idUsuario,
    List<Long> idsLibros,
    LocalDateTime fechaReserva
) {}
