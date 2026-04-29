package com.arrupeteca.dto.response.summary;

public record AutorResumen(
    Long id,
    String nombre,
    String seudonimo,
    Integer anioNacimiento,
    Integer anioFallecimiento,
    boolean borradoLogico,
    String nombrePais,
    String nombreNacionalidad,
    String nombreGeneroPrincipal,
    String nombreObraCumbre
) { }
