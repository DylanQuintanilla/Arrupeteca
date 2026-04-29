package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.LibroResumen;
import java.time.LocalDateTime;
import java.util.Set;

public record IdiomaDetalleResponse(
    Long id,
    String nombre,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    Set<LibroResumen> libros
) { }
