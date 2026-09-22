package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.NacionalidadResponse;
import com.arrupeteca.persistence.entity.Nacionalidad;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface NacionalidadMapper {

    Nacionalidad toEntity(NacionalidadRequest request);

    void updateEntity(NacionalidadRequest request, @MappingTarget Nacionalidad nacionalidad);

    NacionalidadResponse toResponse(Nacionalidad nacionalidad);
}
