package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.detail.IdiomaDetalleResponse;
import com.arrupeteca.dto.response.summary.IdiomaResumen;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.entity.Libro;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IdiomaMapper {

    Idioma toEntity(IdiomaRequest request);

    void updateEntity(IdiomaRequest request, @MappingTarget Idioma idioma);

    IdiomaResumen toResumen(Idioma idioma);

    IdiomaDetalleResponse toDetalleResponse(Idioma idioma);

    default LibroResumen mapLibroToLibroResumen(Libro libro) {
        if (libro == null) {
            return null;
        }
        return new LibroResumen(
            libro.getId(), 
            libro.getIsbn(), 
            libro.getAnioPublicacion(), 
            libro.getPrecio(), 
            libro.getUrlImagen(), 
            libro.getMora(), 
            libro.isBorradoLogico(), 
            libro.getGrado() != null ? libro.getGrado().name() : null, 
            libro.getCiclo() != null ? libro.getCiclo().name() : null, 
            libro.getObra() != null ? libro.getObra().getTitulo() : null, 
            libro.getEditorial() != null ? libro.getEditorial().getNombre() : null, 
            libro.getFormato() != null ? libro.getFormato().getNombre() : null
        );
    }

}
