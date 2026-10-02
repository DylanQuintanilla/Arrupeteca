package com.arrupeteca.prestamo.internal.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.prestamo.dto.request.EstadoSolicitudRequest;
import com.arrupeteca.prestamo.dto.response.EstadoSolicitudResponse;
import com.arrupeteca.prestamo.internal.entity.EstadoSolicitud;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EstadoSolicitudMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    EstadoSolicitud toEntity(EstadoSolicitudRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EstadoSolicitudRequest request, @MappingTarget EstadoSolicitud estadoSolicitud);

    EstadoSolicitudResponse toResponse(EstadoSolicitud estadoSolicitud);
}

