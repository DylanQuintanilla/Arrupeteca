package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.ObraResumen;
import java.time.LocalDateTime;
import java.util.Set;

public record CategoriaDetalleResponse(
    Long id,
    String nombre,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    Set<ObraResumen> obras
) { }
