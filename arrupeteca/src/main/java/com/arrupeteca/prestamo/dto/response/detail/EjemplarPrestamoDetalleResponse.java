package com.arrupeteca.prestamo.dto.response.detail;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Jacksonized
public record EjemplarPrestamoDetalleResponse(
    Long id,
    Long idPrestamo,
    Long idEjemplar,
    String codigoEjemplar,
    String tituloObra,
    Long idEstadoEjemplarPrestamo,
    String estadoEjemplarPrestamo,
    Long idEstadoFisicoEntrega,
    String estadoFisicoEntrega,
    Long idEstadoFisicoDevolucion,
    String estadoFisicoDevolucion,
    LocalDateTime fechaDevolucionReal,
    BigDecimal moraCalculada,
    Boolean moraPagada,
    Boolean exentoMora,
    Long recibidoPorId,
    String recibidoPorNombre,
    Boolean borradoLogico,
    Long version
) {}
