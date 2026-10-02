package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.persistence.entity.Edificio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EdificioMapper {

    Edificio toEntity(EdificioRequest request);

    void updateEntity(EdificioRequest request, @MappingTarget Edificio edificio);

    EdificioResponse toResponse(Edificio edificio);
}
