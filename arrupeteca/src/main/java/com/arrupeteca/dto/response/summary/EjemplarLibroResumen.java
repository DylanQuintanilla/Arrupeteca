package com.arrupeteca.dto.response.summary;

public record EjemplarLibroResumen(
    Long id,
    String comentario,
    boolean borradoLogico,
    String nombreEstadoFisico,
    String nombreDisponibilidad,
    String tituloLibro,
    String nombreMueble
) { }
