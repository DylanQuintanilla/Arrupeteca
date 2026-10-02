package com.arrupeteca.prestamo.dto.response.summary;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
public record SolicitudPrestamoResumen(
    Long id,
    Long idUsuario,
    String nombreUsuario,
    Long idEstadoSolicitud,
    String estadoSolicitud,
    LocalDateTime fechaSolicitud,
    LocalDateTime fechaLimiteRetiro,
    boolean creadoPorAdmin,
    boolean borradoLogico
) { }
