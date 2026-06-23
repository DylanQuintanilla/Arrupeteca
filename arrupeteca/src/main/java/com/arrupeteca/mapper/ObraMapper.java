package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.persistence.entity.Obra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import org.mapstruct.Named;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {GeneroMapper.class, CategoriaMapper.class, ObraAutorMapper.class})
public interface ObraMapper {

    Obra toEntity(ObraRequest request);

    void updateEntity(ObraRequest request, @MappingTarget Obra obra);

    @Named("toResumen")
    ObraResumen toResumen(Obra obra);

    @Mapping(target = "autores", source = "obraAutores")
    ObraDetalleResponse toDetalleResponse(Obra obra);

}
