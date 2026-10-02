package com.arrupeteca.prestamo.event;

import java.time.LocalDateTime;

public record EjemplarPerdidoORepuestoEvent(
    Long idEjemplarPrestamo,
    Long idEjemplar,
    Long idUsuario,
    String tipoAccion,
    LocalDateTime fechaEvento
) {}
