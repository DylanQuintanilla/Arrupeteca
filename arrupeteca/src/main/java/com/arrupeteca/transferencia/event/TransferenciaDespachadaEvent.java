package com.arrupeteca.transferencia.event;

import java.time.LocalDateTime;
import java.util.List;

public record TransferenciaDespachadaEvent(
    Long idTransferencia,
    Long idSolicitud,
    Long idMuebleOrigen,
    Long idMuebleDestino,
    Long idUsuarioEnvia,
    List<Long> idsEjemplares,
    LocalDateTime fechaEnvio
) {}
