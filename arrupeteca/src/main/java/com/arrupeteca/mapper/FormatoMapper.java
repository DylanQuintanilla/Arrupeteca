package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.FormatoResponse;
import com.arrupeteca.persistence.entity.Formato;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface FormatoMapper {

    Formato toEntity(FormatoRequest request);

    void updateEntity(FormatoRequest request, @MappingTarget Formato formato);

    FormatoResponse toResponse(Formato formato);
}
