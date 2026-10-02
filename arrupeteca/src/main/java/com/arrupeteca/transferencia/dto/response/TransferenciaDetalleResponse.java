package com.arrupeteca.transferencia.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Jacksonized
public record TransferenciaDetalleResponse(
    @NotNull Long id,
    Long version,
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
    String observacionRecepcion,
    boolean confirmada,
    String motivo,
    boolean borradoLogico,
    List<EjemplarTransferenciaResponse> ejemplares,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion,
    String creadoPor,
    String actualizadoPor
) { }
