package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.persistence.entity.Libro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ObraMapper.class, EditorialMapper.class, FormatoMapper.class, IdiomaMapper.class})
public interface LibroMapper {

    Libro toEntity(LibroRequest request);

    void updateEntity(LibroRequest request, @MappingTarget Libro libro);

    @Mapping(target = "tituloObra", source = "obra.titulo")
    @Mapping(target = "nombreEditorial", source = "editorial.nombre")
    @Mapping(target = "nombreFormato", source = "formato.nombre")
    LibroResumen toResumen(Libro libro);

    LibroDetalleResponse toDetalleResponse(Libro libro);

}
