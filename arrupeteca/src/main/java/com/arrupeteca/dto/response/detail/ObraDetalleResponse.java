package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.dto.response.GeneroResponse;
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
    Set<GeneroResponse> generos,
    Set<CategoriaResponse> categorias,
    Set<ObraAutorDetalleResponse> autores
) { }
