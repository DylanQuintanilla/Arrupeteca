package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.persistence.entity.Categoria;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface CategoriaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "obras", ignore = true)
    Categoria toEntity(CategoriaRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(CategoriaRequest request, @MappingTarget Categoria categoria);

    CategoriaResponse toResponse(Categoria categoria);
}
