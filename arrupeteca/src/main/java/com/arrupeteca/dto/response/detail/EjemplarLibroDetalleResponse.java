package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.DisponibilidadResponse;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record EjemplarLibroDetalleResponse(
    Long id,
    String comentario,
    Long version,
    boolean borradoLogico,
    EstadoFisicoResponse estadoFisico,
    DisponibilidadResponse disponibilidad,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    LibroResumen libro,
    MuebleResponse mueble,
    String ubicacion
) { }
