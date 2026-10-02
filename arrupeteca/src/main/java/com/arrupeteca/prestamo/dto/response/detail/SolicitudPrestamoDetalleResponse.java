package com.arrupeteca.prestamo.dto.response.detail;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
public record SolicitudPrestamoDetalleResponse(
    Long id,
    Long version,
    Long idUsuario,
    String nombreUsuario,
    Long idEstadoSolicitud,
    String estadoSolicitud,
    LocalDateTime fechaSolicitud,
    LocalDateTime fechaLimiteRetiro,
    LocalDateTime fechaRevision,
    Long revisadoPorId,
    String revisadoPorNombre,
    String comentarioRespuesta,
    boolean creadoPorAdmin,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String actualizadoPor,
    LocalDateTime fechaActualizacion
) { }
