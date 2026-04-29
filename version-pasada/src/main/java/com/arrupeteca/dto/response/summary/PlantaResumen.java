package com.arrupeteca.dto.response.summary;

public record PlantaResumen(
    Long id,
    String nombre,
    boolean borradoLogico,
    String nombreEdificio
) { }
