package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.PlantaResumen;
import java.time.LocalDateTime;

public record SalonDetalleResponse(
    Long id,
    String nombre,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    PlantaResumen planta
) { }
