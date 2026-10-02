package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.PaisResponse;
import com.arrupeteca.persistence.entity.Pais;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface PaisMapper {

    Pais toEntity(PaisRequest request);

    void updateEntity(PaisRequest request, @MappingTarget Pais pais);

    PaisResponse toResponse(Pais pais);
}
