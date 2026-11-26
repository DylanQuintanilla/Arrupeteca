package com.arrupeteca.prestamo.dto.response.detail;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
public record SolicitudRenovacionDetalleResponse(
    Long id,
    Long idEjemplarPrestamo,
    Long idUsuario,
    String nombreUsuario,
    Long idEstadoSolicitud,
    String estadoSolicitud,
    Integer diasSolicitados,
    LocalDateTime nuevaFechaLimitePropuesta,
    LocalDateTime fechaSolicitud,
    LocalDateTime fechaRevision,
    Long revisadoPorId,
    String revisadoPorNombre,
    String motivoSolicitud,
    String motivoRespuesta,
    Boolean borradoLogico,
    Long version
) {}
