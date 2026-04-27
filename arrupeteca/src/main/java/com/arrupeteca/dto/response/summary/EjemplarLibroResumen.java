package com.arrupeteca.dto.response.summary;

public record EjemplarLibroResumen(
    Long id,
    String comentario,
    boolean borradoLogico,
    String estadoFisico,
    String disponibilidad,
    String tituloLibro,
    String nombreMueble
) { }
