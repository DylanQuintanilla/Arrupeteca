package com.arrupeteca.dto.response.detail;

import java.time.LocalDateTime;

public record EdificioDetalleResponse(
    Long id,
    String nombre,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion
) { }
