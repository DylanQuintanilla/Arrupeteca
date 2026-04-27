package com.arrupeteca.dto.response.summary;

public record SalonResumen(
    Long id,
    String nombre,
    boolean borradoLogico,
    String nombrePlanta
) { }
