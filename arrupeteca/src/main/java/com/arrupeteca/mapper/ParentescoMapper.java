package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.ParentescoRequest;
import com.arrupeteca.dto.response.ParentescoResponse;
import com.arrupeteca.persistence.entity.Parentesco;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface ParentescoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    Parentesco toEntity(ParentescoRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ParentescoRequest request, @MappingTarget Parentesco parentesco);

    ParentescoResponse toResponse(Parentesco parentesco);

}
