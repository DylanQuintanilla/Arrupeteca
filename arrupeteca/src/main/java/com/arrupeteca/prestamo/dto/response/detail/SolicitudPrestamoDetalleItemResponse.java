package com.arrupeteca.prestamo.dto.response.detail;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record SolicitudPrestamoDetalleItemResponse(
    Long id,
    Long idSolicitud,
    Long idLibro,
    String tituloObra,
    String isbnLibro,
    Integer cantidadSolicitada,
    Boolean borradoLogico
) {}
