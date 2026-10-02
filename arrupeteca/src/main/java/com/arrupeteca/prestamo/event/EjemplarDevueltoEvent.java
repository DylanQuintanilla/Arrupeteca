package com.arrupeteca.prestamo.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EjemplarDevueltoEvent(
    Long idEjemplarPrestamo,
    Long idPrestamo,
    Long idEjemplar,
    Long idUsuario,
    boolean conMora,
    BigDecimal moraCalculada,
    LocalDateTime fechaDevolucion
) {}
