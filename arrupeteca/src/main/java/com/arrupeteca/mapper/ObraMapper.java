package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.persistence.entity.Obra;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(
    config = CentralMapperConfig.class,
    uses = {GeneroMapper.class, CategoriaMapper.class, ObraAutorMapper.class}
)
public interface ObraMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "generos", ignore = true)
    @Mapping(target = "categorias", ignore = true)
    @Mapping(target = "obraAutores", ignore = true)
    @Mapping(target = "version", ignore = true)
    Obra toEntity(ObraRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ObraRequest request, @MappingTarget Obra obra);

    @Named("toResumen")
    ObraResumen toResumen(Obra obra);

    @Mapping(target = "autores", source = "obraAutores")
    ObraDetalleResponse toDetalleResponse(Obra obra);
}
