package com.arrupeteca.transferencia.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Jacksonized
public record SolicitudTransferenciaDetalleResponse(
    @NotNull Long id,
    Long version,
    @NotNull Long idUsuario,
    String nombreUsuario,
    @NotNull Long idSalonDestino,
    String nombreSalonDestino,
    @NotNull Long idEstadoSolicitudTransferencia,
    String nombreEstadoSolicitudTransferencia,
    String motivo,
    String motivoRechazo,
    boolean borradoLogico,
    List<DetalleSolicitudTransferenciaResponse> detalles,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion,
    String creadoPor,
    String actualizadoPor
) { }
