package com.arrupeteca.prestamo.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.prestamo.dto.request.EjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.EjemplarPrestamoDetalleResponse;
import com.arrupeteca.prestamo.persistence.entity.EjemplarPrestamo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EjemplarPrestamoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "prestamo", ignore = true)
    @Mapping(target = "ejemplar", ignore = true)
    @Mapping(target = "estadoEjemplarPrestamo", ignore = true)
    @Mapping(target = "estadoFisicoEntrega", ignore = true)
    @Mapping(target = "estadoFisicoDevolucion", ignore = true)
    @Mapping(target = "fechaDevolucionReal", ignore = true)
    @Mapping(target = "moraCalculada", ignore = true)
    @Mapping(target = "moraPagada", ignore = true)
    @Mapping(target = "exentoMora", ignore = true)
    @Mapping(target = "recibidoPor", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    EjemplarPrestamo toEntity(EjemplarPrestamoRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EjemplarPrestamoRequest request, @MappingTarget EjemplarPrestamo entity);

    @Mapping(target = "idPrestamo", source = "prestamo.id")
    @Mapping(target = "idEjemplar", source = "ejemplar.id")
    @Mapping(target = "codigoEjemplar", source = "ejemplar.id")
    @Mapping(target = "tituloObra", expression = "java(obtenerTituloObra(entity.getEjemplar()))")
    @Mapping(target = "idEstadoEjemplarPrestamo", source = "estadoEjemplarPrestamo.id")
    @Mapping(target = "estadoEjemplarPrestamo", source = "estadoEjemplarPrestamo.nombre")
    @Mapping(target = "idEstadoFisicoEntrega", source = "estadoFisicoEntrega.id")
    @Mapping(target = "estadoFisicoEntrega", source = "estadoFisicoEntrega.nombre")
    @Mapping(target = "idEstadoFisicoDevolucion", source = "estadoFisicoDevolucion.id")
    @Mapping(target = "estadoFisicoDevolucion", source = "estadoFisicoDevolucion.nombre")
    @Mapping(target = "recibidoPorId", source = "recibidoPor.id")
    @Mapping(target = "recibidoPorNombre", expression = "java(obtenerNombreCompleto(entity.getRecibidoPor()))")
    EjemplarPrestamoDetalleResponse toDetalleResponse(EjemplarPrestamo entity);

    default String obtenerTituloObra(EjemplarLibro ejemplar) {
        if (ejemplar == null || ejemplar.getLibro() == null || ejemplar.getLibro().getObra() == null) {
            return null;
        }
        return ejemplar.getLibro().getObra().getTitulo();
    }

    default String obtenerNombreCompleto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        String nombres = usuario.getNombres() != null ? usuario.getNombres().trim() : "";
        String apellidos = usuario.getApellidos() != null ? usuario.getApellidos().trim() : "";
        return (nombres + " " + apellidos).trim();
    }
}
