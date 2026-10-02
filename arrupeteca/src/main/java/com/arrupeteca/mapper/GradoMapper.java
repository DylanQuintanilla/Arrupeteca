package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.GradoRequest;
import com.arrupeteca.dto.response.GradoResponse;
import com.arrupeteca.persistence.entity.Grado;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface GradoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    Grado toEntity(GradoRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(GradoRequest request, @MappingTarget Grado grado);

    GradoResponse toResponse(Grado grado);
}
