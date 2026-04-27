package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.dto.response.summary.MuebleResumen;
import java.time.LocalDateTime;

public record EjemplarLibroDetalleResponse(
    Long id,
    String comentario,
    boolean borradoLogico,
    String estadoFisico,
    String disponibilidad,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    LibroResumen libro,
    MuebleResumen mueble,
    String ubicacion
) { }
