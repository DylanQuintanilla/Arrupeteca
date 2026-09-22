package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.EtiquetaRequest;
import com.arrupeteca.dto.response.EtiquetaResponse;
import com.arrupeteca.persistence.entity.Etiqueta;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EtiquetaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    Etiqueta toEntity(EtiquetaRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EtiquetaRequest request, @MappingTarget Etiqueta entity);

    EtiquetaResponse toResponse(Etiqueta entity);
}
