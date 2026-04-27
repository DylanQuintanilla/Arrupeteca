package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
import com.arrupeteca.persistence.entity.Autor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PaisMapper.class, NacionalidadMapper.class, GeneroMapper.class})
public interface AutorMapper {

    Autor toEntity(AutorRequest request);

    void updateEntity(AutorRequest request, @MappingTarget Autor autor);

    @Mapping(target = "nombrePais", source = "paisNacimiento.nombre")
    @Mapping(target = "nombreNacionalidad", source = "nacionalidad.nombre")
    AutorResumen toResumen(Autor autor);

    AutorDetalleResponse toDetalleResponse(Autor autor);

}
