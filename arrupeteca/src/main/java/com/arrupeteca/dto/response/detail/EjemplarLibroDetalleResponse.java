package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
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
    MuebleResponse mueble,
    String ubicacion
) { }
