package com.arrupeteca.prestamo.dto.response.summary;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
public record SolicitudRenovacionResumen(
    Long id,
    Long idEjemplarPrestamo,
    Long idUsuario,
    String nombreUsuario,
    Long idEstadoSolicitud,
    String estadoSolicitud,
    Integer diasSolicitados,
    LocalDateTime fechaSolicitud,
    Boolean borradoLogico
) {}
