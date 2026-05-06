package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
import com.arrupeteca.persistence.entity.Autor;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        PaisMapper.class,
        NacionalidadMapper.class,
        GeneroMapper.class,
        ObraMapper.class
    }
)
public interface AutorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "paisNacimiento", ignore = true)
    @Mapping(target = "nacionalidad", ignore = true)
    @Mapping(target = "generoPrincipal", ignore = true)
    @Mapping(target = "obraCumbre", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "modificadoPor", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Autor toEntity(AutorRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(AutorRequest request, @MappingTarget Autor autor);

    @Mapping(target = "nombrePais", source = "paisNacimiento.nombre")
    @Mapping(target = "nombreNacionalidad", source = "nacionalidad.nombre")
    @Mapping(target = "nombreGeneroPrincipal", source = "generoPrincipal.nombre")
    @Mapping(target = "nombreObraCumbre", source = "obraCumbre.titulo")
    AutorResumen toResumen(Autor autor);

    @Mapping(target = "paisNacimiento", source = "paisNacimiento")
    @Mapping(target = "nacionalidad", source = "nacionalidad")
    @Mapping(target = "generoPrincipal", source = "generoPrincipal")
    @Mapping(target = "obraCumbre", source = "obraCumbre")
    AutorDetalleResponse toDetalleResponse(Autor autor);
}
