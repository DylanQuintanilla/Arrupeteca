package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.GeneroResponse;
import com.arrupeteca.persistence.entity.Genero;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface GeneroMapper {

    Genero toEntity(GeneroRequest request);

    void updateEntity(GeneroRequest request, @MappingTarget Genero genero);

    GeneroResponse toResponse(Genero genero);
}
