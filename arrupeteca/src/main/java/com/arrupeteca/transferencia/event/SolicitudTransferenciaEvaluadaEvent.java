package com.arrupeteca.transferencia.event;

import java.time.LocalDateTime;

public record SolicitudTransferenciaEvaluadaEvent(
    Long idSolicitud,
    Long idUsuarioSolicitante,
    Long idEstadoSolicitud,
    boolean aprobada,
    String motivoRechazo,
    LocalDateTime fechaEvaluacion
) {}
