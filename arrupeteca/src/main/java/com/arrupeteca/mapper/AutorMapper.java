package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
import com.arrupeteca.persistence.entity.Autor;
import org.mapstruct.*;

@Mapper(
    config = CentralMapperConfig.class,
    uses = {
        PaisMapper.class,
        NacionalidadMapper.class,
        GeneroMapper.class,
        ObraMapper.class
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
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "actualizadoPor", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Autor toEntity(AutorRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(AutorRequest request, @MappingTarget Autor autor);

    @Mapping(target = "nombrePais", source = "paisNacimiento.nombre")
    @Mapping(target = "nombreNacionalidad", source = "nacionalidad.nombre")
    @Mapping(target = "nombreGeneroPrincipal", source = "generoPrincipal.nombre")
    @Mapping(target = "nombreObraCumbre", source = "obraCumbre.titulo")
    @Mapping(target = "nombre", source = "nombre", qualifiedByName = "trimString")
    @Mapping(target = "seudonimo", source = "seudonimo", qualifiedByName = "trimString")
    @Mapping(target = "biografiaResumida", ignore = true) // Se calcula en @AfterMapping
    AutorResumen toResumen(Autor autor);

    @Mapping(target = "paisNacimiento", source = "paisNacimiento")
    @Mapping(target = "nacionalidad", source = "nacionalidad")
    @Mapping(target = "generoPrincipal", source = "generoPrincipal")
    @Mapping(target = "obraCumbre", source = "obraCumbre")
    @Mapping(target = "nombre", source = "nombre", qualifiedByName = "trimString")
    @Mapping(target = "seudonimo", source = "seudonimo", qualifiedByName = "trimString")
    AutorDetalleResponse toDetalleResponse(Autor autor);

    @Named("trimString")
    default String trimString(String value) {
        return (value == null) ? null : value.trim();
    }

    @AfterMapping
    default void finalizeResumen(@MappingTarget AutorResumen.AutorResumenBuilder resumen, Autor autor) {
        String biografia = autor.getBiografia();
        if (biografia != null && !biografia.isBlank()) {
            String extracto = biografia.length() > 100 
                ? biografia.substring(0, 100).trim() + "..." 
                : biografia.trim();
            resumen.biografiaResumida(extracto);
        }
    }
}
