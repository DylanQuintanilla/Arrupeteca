package com.arrupeteca.mapper;

import com.arrupeteca.dto.response.detail.ObraAutorDetalleResponse;
import com.arrupeteca.persistence.entity.ObraAutor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AutorMapper.class, TipoAutoriaMapper.class})
public interface ObraAutorMapper {

    ObraAutorDetalleResponse toDetalleResponse(ObraAutor obraAutor);

}
