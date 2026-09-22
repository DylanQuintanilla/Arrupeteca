package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import com.arrupeteca.persistence.entity.TipoAutoria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface TipoAutoriaMapper {

    TipoAutoria toEntity(TipoAutoriaRequest request);

    void updateEntity(TipoAutoriaRequest request, @MappingTarget TipoAutoria tipoAutoria);

    TipoAutoriaResponse toResponse(TipoAutoria tipoAutoria);
}
