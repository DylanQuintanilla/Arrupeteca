package com.arrupeteca.transferencia.event;

import java.time.LocalDateTime;

public record SolicitudTransferenciaCreadaEvent(
    Long idSolicitud,
    Long idUsuarioSolicitante,
    Long idSalonDestino,
    LocalDateTime fechaCreacion,
    Integer totalLibrosSolicitados
) {}
