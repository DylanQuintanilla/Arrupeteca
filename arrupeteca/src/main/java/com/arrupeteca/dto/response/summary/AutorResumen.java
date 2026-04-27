package com.arrupeteca.dto.response.summary;

public record AutorResumen(
    Long id,
    String nombre,
    String seudonimo,
    Integer anioNacimiento,
    Integer anioFallecimiento,
    String biografia,
    boolean borradoLogico,
    String nombrePais,
    String nombreNacionalidad
) { }
