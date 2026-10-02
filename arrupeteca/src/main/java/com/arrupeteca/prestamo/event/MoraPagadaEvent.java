package com.arrupeteca.prestamo.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MoraPagadaEvent(
    Long idEjemplarPrestamo,
    Long idUsuario,
    BigDecimal montoPagado,
    LocalDateTime fechaPago
) {}
