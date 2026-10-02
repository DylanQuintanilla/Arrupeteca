package com.arrupeteca.transferencia.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
public record TransferenciaResumenResponse(
    @NotNull Long id,
    Long idSolicitudTransferencia,
    @NotNull Long idUsuarioEnvia,
    String nombreUsuarioEnvia,
    Long idUsuarioRecibe,
    String nombreUsuarioRecibe,
    @NotNull Long idMuebleOrigen,
    String nombreMuebleOrigen,
    @NotNull Long idMuebleDestino,
    String nombreMuebleDestino,
    @NotNull Long idEstadoTransferencia,
    String nombreEstadoTransferencia,
    LocalDateTime fechaEnvio,
    LocalDateTime fechaRecepcion,
    boolean confirmada,
    Integer cantidadEjemplares,
    boolean borradoLogico,
    LocalDateTime fechaCreacion
) { }
