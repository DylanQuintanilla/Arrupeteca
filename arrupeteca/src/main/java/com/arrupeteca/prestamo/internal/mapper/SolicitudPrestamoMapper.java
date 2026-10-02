package com.arrupeteca.prestamo.internal.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudPrestamoResumen;
import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface SolicitudPrestamoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "estadoSolicitud", ignore = true)
    @Mapping(target = "fechaSolicitud", ignore = true)
    @Mapping(target = "fechaRevision", ignore = true)
    @Mapping(target = "revisadoPor", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    SolicitudPrestamo toEntity(SolicitudPrestamoRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(SolicitudPrestamoRequest request, @MappingTarget SolicitudPrestamo entity);

    @Mapping(target = "idUsuario", source = "usuario.id")
    @Mapping(target = "nombreUsuario", expression = "java(obtenerNombreCompleto(entity.getUsuario()))")
    @Mapping(target = "idEstadoSolicitud", source = "estadoSolicitud.id")
    @Mapping(target = "estadoSolicitud", source = "estadoSolicitud.nombre")
    SolicitudPrestamoResumen toResumen(SolicitudPrestamo entity);

    @Mapping(target = "idUsuario", source = "usuario.id")
    @Mapping(target = "nombreUsuario", expression = "java(obtenerNombreCompleto(entity.getUsuario()))")
    @Mapping(target = "idEstadoSolicitud", source = "estadoSolicitud.id")
    @Mapping(target = "estadoSolicitud", source = "estadoSolicitud.nombre")
    @Mapping(target = "revisadoPorId", source = "revisadoPor.id")
    @Mapping(target = "revisadoPorNombre", expression = "java(obtenerNombreCompleto(entity.getRevisadoPor()))")
    SolicitudPrestamoDetalleResponse toDetalleResponse(SolicitudPrestamo entity);

    default String obtenerNombreCompleto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        String nombres = usuario.getNombres() != null ? usuario.getNombres().trim() : "";
        String apellidos = usuario.getApellidos() != null ? usuario.getApellidos().trim() : "";
        return (nombres + " " + apellidos).trim();
    }
}
