package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.IdiomaResponse;
import com.arrupeteca.persistence.entity.Idioma;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface IdiomaMapper {

    Idioma toEntity(IdiomaRequest request);

    void updateEntity(IdiomaRequest request, @MappingTarget Idioma idioma);

    IdiomaResponse toResponse(Idioma idioma);
}
