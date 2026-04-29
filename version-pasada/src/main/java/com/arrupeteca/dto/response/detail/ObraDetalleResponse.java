package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.CategoriaResumen;
import com.arrupeteca.dto.response.summary.GeneroResumen;
import java.time.LocalDateTime;
import java.util.Set;

public record ObraDetalleResponse(
    Long id,
    String titulo,
    Integer anioCreacion,
    String resumen,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    Set<GeneroResumen> generos,
    Set<CategoriaResumen> categorias,
    Set<ObraAutorDetalleResponse> autores
) { }
