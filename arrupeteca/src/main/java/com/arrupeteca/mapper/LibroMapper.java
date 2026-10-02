package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.persistence.entity.Libro;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    config = CentralMapperConfig.class,
    uses = {ObraMapper.class, EditorialMapper.class, FormatoMapper.class, IdiomaMapper.class, GradoMapper.class, CicloMapper.class}
)
public interface LibroMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "grado", ignore = true)
    @Mapping(target = "ciclo", ignore = true)
    @Mapping(target = "obra", ignore = true)
    @Mapping(target = "editorial", ignore = true)
    @Mapping(target = "formato", ignore = true)
    @Mapping(target = "idiomas", ignore = true)
    @Mapping(target = "version", ignore = true)
    Libro toEntity(LibroRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(LibroRequest request, @MappingTarget Libro libro);

    @Mapping(target = "nombreGrado", source = "grado.nombre")
    @Mapping(target = "nombreCiclo", source = "ciclo.nombre")
    @Mapping(target = "tituloObra", source = "obra.titulo")
    @Mapping(target = "nombreEditorial", source = "editorial.nombre")
    @Mapping(target = "nombreFormato", source = "formato.nombre")
    LibroResumen toResumen(Libro libro);

    LibroDetalleResponse toDetalleResponse(Libro libro);
}
