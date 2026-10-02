package com.arrupeteca.prestamo.dto.response.detail;

import lombok.Builder;

@Builder
public record SolicitudPrestamoDetalleItemResponse(
    Long id,
    Long idSolicitud,
    Long idLibro,
    String tituloObra,
    String isbnLibro,
    Integer cantidadSolicitada,
    Boolean borradoLogico
) {}
