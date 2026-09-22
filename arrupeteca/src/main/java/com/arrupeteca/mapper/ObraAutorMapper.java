package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.ObraAutorRequest;
import com.arrupeteca.dto.response.detail.ObraAutorDetalleResponse;
import com.arrupeteca.persistence.entity.ObraAutor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    config = CentralMapperConfig.class,
    uses = {AutorMapper.class, TipoAutoriaMapper.class}
)
public interface ObraAutorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "obra", ignore = true)
    @Mapping(target = "autor", ignore = true)
    @Mapping(target = "tipoAutoria", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    ObraAutor toEntity(ObraAutorRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "obra", ignore = true)
    @Mapping(target = "autor", ignore = true)
    @Mapping(target = "tipoAutoria", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    void updateEntity(ObraAutorRequest request, @MappingTarget ObraAutor obraAutor);

    ObraAutorDetalleResponse toDetalleResponse(ObraAutor obraAutor);
}
