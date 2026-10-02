package com.arrupeteca.transferencia.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
public record SolicitudTransferenciaResumenResponse(
    @NotNull Long id,
    @NotNull Long idUsuario,
    String nombreUsuario,
    @NotNull Long idSalonDestino,
    String nombreSalonDestino,
    @NotNull Long idEstadoSolicitudTransferencia,
    String nombreEstadoSolicitudTransferencia,
    String motivo,
    String motivoRechazo,
    Integer cantidadTotalLibros,
    boolean borradoLogico,
    LocalDateTime fechaCreacion
) { }
