package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.SalonResumen;
import java.time.LocalDateTime;

public record MuebleDetalleResponse(
    Long id,
    String nombre,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    SalonResumen salon
) { }
