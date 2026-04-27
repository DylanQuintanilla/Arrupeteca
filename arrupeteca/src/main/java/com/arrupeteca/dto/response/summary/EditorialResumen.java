package com.arrupeteca.dto.response.summary;

public record EditorialResumen(
    Long id,
    String nombre,
    String direccion,
    String correo,
    String telefono,
    String personaContacto,
    boolean borradoLogico
) { }
