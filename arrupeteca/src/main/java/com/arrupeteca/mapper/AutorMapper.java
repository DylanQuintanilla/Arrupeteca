package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.dto.response.summary.ObraResumen;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(
    config = CentralMapperConfig.class,
    uses = {
        PaisMapper.class,
        NacionalidadMapper.class,
        GeneroMapper.class
    }
)
public interface AutorMapper {

    /**
     * NOTA ARQUITECTURAL: 
     * Se utilizan DTOs de "Resumen" para las relaciones en las respuestas de detalle 
     * para cortar ciclos infinitos de mapeo. Si en el futuro se requieren DTOs 
     * recursivos, se debe implementar un 'CycleAvoidingMappingContext'.
     */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "paisNacimiento", ignore = true)
    @Mapping(target = "nacionalidad", ignore = true)
    @Mapping(target = "generoPrincipal", ignore = true)
    @Mapping(target = "obraCumbre", ignore = true)
    @Mapping(target = "version", ignore = true)
    Autor toEntity(AutorRequest request);

    @InheritConfiguration(name = "toEntity")
    @Mapping(target = "version", ignore = true)
    void updateEntity(AutorRequest request, @MappingTarget Autor autor);

    @Mapping(target = "nombrePais", source = "paisNacimiento.nombre")
    @Mapping(target = "nombreNacionalidad", source = "nacionalidad.nombre")
    @Mapping(target = "nombreGeneroPrincipal", source = "generoPrincipal.nombre")
    @Mapping(target = "nombreObraCumbre", source = "obraCumbre.titulo")
    @Mapping(target = "biografiaResumida", source = "biografia", qualifiedByName = "generarExtractoBiografia")
    AutorResumenResponse toResumen(Autor autor);

    @Mapping(target = "paisNacimiento", source = "paisNacimiento")
    @Mapping(target = "nacionalidad", source = "nacionalidad")
    @Mapping(target = "generoPrincipal", source = "generoPrincipal")
    @Mapping(target = "obraCumbre", source = "obraCumbre", qualifiedByName = "obraToResumen")
    AutorDetalleResponse toDetalleResponse(Autor autor);

    @Named("obraToResumen")
    default ObraResumen obraToResumen(Obra obra) {
        if (obra == null) {
            return null;
        }
        return new ObraResumen(
            obra.getId(),
            obra.getTitulo(),
            obra.getAnioCreacion(),
            obra.getResumen(),
            obra.isBorradoLogico()
        );
    }

    @Named("generarExtractoBiografia")
    default String generarExtractoBiografia(String biografia) {
        if (biografia == null || biografia.isBlank()) {
            return null;
        }
        return biografia.length() > 100 
            ? biografia.substring(0, 100).trim() + "..." 
            : biografia.trim();
    }
}
