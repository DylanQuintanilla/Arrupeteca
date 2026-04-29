package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.detail.CategoriaDetalleResponse;
import com.arrupeteca.dto.response.summary.CategoriaResumen;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Obra;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoriaMapper {

    Categoria toEntity(CategoriaRequest request);

    void updateEntity(CategoriaRequest request, @MappingTarget Categoria categoria);

    CategoriaResumen toResumen(Categoria categoria);

    CategoriaDetalleResponse toDetalleResponse(Categoria categoria);

    default ObraResumen mapObraToObraResumen(Obra obra) {
        if (obra == null) {
            return null;
        }
        return new ObraResumen(obra.getId(), obra.getTitulo(), obra.getAnioCreacion(), obra.getResumen(), obra.isBorradoLogico());
    }

}
