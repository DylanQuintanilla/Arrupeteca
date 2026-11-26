package com.arrupeteca.prestamo.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.prestamo.dto.request.PrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.PrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.PrestamoResumen;
import com.arrupeteca.prestamo.persistence.entity.Prestamo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface PrestamoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "solicitudPrestamo", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "estadoPrestamo", ignore = true)
    @Mapping(target = "fechaPrestamo", ignore = true)
    @Mapping(target = "fechaLimiteDevolucion", ignore = true)
    @Mapping(target = "entregadoPor", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    Prestamo toEntity(PrestamoRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(PrestamoRequest request, @MappingTarget Prestamo entity);

    @Mapping(target = "idSolicitud", source = "solicitudPrestamo.id")
    @Mapping(target = "idUsuario", source = "usuario.id")
    @Mapping(target = "nombreUsuario", expression = "java(obtenerNombreCompleto(entity.getUsuario()))")
    @Mapping(target = "idEstadoPrestamo", source = "estadoPrestamo.id")
    @Mapping(target = "estadoPrestamo", source = "estadoPrestamo.nombre")
    PrestamoResumen toResumen(Prestamo entity);

    @Mapping(target = "idSolicitud", source = "solicitudPrestamo.id")
    @Mapping(target = "idUsuario", source = "usuario.id")
    @Mapping(target = "nombreUsuario", expression = "java(obtenerNombreCompleto(entity.getUsuario()))")
    @Mapping(target = "idEstadoPrestamo", source = "estadoPrestamo.id")
    @Mapping(target = "estadoPrestamo", source = "estadoPrestamo.nombre")
    @Mapping(target = "entregadoPorId", source = "entregadoPor.id")
    @Mapping(target = "entregadoPorNombre", expression = "java(obtenerNombreCompleto(entity.getEntregadoPor()))")
    PrestamoDetalleResponse toDetalleResponse(Prestamo entity);

    default String obtenerNombreCompleto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        String nombres = usuario.getNombres() != null ? usuario.getNombres().trim() : "";
        String apellidos = usuario.getApellidos() != null ? usuario.getApellidos().trim() : "";
        return (nombres + " " + apellidos).trim();
    }
}
