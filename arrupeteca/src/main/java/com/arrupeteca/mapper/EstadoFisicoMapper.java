package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.EstadoFisicoRequest;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import com.arrupeteca.persistence.entity.EstadoFisico;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EstadoFisicoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    EstadoFisico toEntity(EstadoFisicoRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EstadoFisicoRequest request, @MappingTarget EstadoFisico estadoFisico);

    EstadoFisicoResponse toResponse(EstadoFisico estadoFisico);
}
