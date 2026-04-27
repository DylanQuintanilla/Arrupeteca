package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.EdificioResumen;
import java.time.LocalDateTime;

public record PlantaDetalleResponse(
    Long id,
    String nombre,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    EdificioResumen edificio
) { }
