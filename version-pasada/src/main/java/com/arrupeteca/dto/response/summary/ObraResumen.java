package com.arrupeteca.dto.response.summary;

public record ObraResumen(
    Long id,
    String titulo,
    Integer anioCreacion,
    String resumen,
    boolean borradoLogico
) { }
