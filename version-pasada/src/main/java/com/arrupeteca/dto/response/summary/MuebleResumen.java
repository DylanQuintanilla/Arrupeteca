package com.arrupeteca.dto.response.summary;

public record MuebleResumen(
    Long id,
    String nombre,
    boolean borradoLogico,
    String nombreSalon
) { }
