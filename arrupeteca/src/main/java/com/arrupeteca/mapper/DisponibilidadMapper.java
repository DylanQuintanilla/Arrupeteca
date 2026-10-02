package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.DisponibilidadRequest;
import com.arrupeteca.dto.response.DisponibilidadResponse;
import com.arrupeteca.persistence.entity.Disponibilidad;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface DisponibilidadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    Disponibilidad toEntity(DisponibilidadRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(DisponibilidadRequest request, @MappingTarget Disponibilidad disponibilidad);

    DisponibilidadResponse toResponse(Disponibilidad disponibilidad);
}
