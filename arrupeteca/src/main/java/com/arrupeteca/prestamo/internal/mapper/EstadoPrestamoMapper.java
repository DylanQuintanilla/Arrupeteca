package com.arrupeteca.prestamo.internal.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.prestamo.dto.request.EstadoPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoPrestamoResponse;
import com.arrupeteca.prestamo.internal.entity.EstadoPrestamo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EstadoPrestamoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    EstadoPrestamo toEntity(EstadoPrestamoRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EstadoPrestamoRequest request, @MappingTarget EstadoPrestamo estadoPrestamo);

    EstadoPrestamoResponse toResponse(EstadoPrestamo estadoPrestamo);
}

