package com.arrupeteca.prestamo.internal.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoDetalleRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleItemResponse;
import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamoDetalle;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface SolicitudPrestamoDetalleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "solicitudPrestamo", ignore = true)
    @Mapping(target = "libro", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    SolicitudPrestamoDetalle toEntity(SolicitudPrestamoDetalleRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(SolicitudPrestamoDetalleRequest request, @MappingTarget SolicitudPrestamoDetalle entity);

    @Mapping(target = "idSolicitud", source = "solicitudPrestamo.id")
    @Mapping(target = "idLibro", source = "libro.id")
    @Mapping(target = "tituloObra", expression = "java(obtenerTituloObra(entity.getLibro()))")
    @Mapping(target = "isbnLibro", source = "libro.isbn")
    SolicitudPrestamoDetalleItemResponse toResponse(SolicitudPrestamoDetalle entity);

    default String obtenerTituloObra(Libro libro) {
        if (libro == null || libro.getObra() == null) {
            return null;
        }
        return libro.getObra().getTitulo();
    }
}
