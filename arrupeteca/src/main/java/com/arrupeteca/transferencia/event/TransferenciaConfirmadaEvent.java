package com.arrupeteca.transferencia.event;

import java.time.LocalDateTime;
import java.util.List;

public record TransferenciaConfirmadaEvent(
    Long idTransferencia,
    Long idSolicitudOriginaria,
    Long idMuebleDestino,
    Long idUsuarioRecibe,
    List<Long> idsEjemplaresRecibidos,
    String observacionRecepcion,
    LocalDateTime fechaRecepcion
) {}
