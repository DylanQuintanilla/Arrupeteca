package com.arrupeteca.prestamo.internal.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.prestamo.dto.request.EstadoEjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoEjemplarPrestamoResponse;
import com.arrupeteca.prestamo.internal.entity.EstadoEjemplarPrestamo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EstadoEjemplarPrestamoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    EstadoEjemplarPrestamo toEntity(EstadoEjemplarPrestamoRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EstadoEjemplarPrestamoRequest request, @MappingTarget EstadoEjemplarPrestamo estadoEjemplarPrestamo);

    EstadoEjemplarPrestamoResponse toResponse(EstadoEjemplarPrestamo estadoEjemplarPrestamo);
}

