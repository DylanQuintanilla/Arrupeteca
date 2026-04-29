package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.detail.GeneroDetalleResponse;
import com.arrupeteca.dto.response.summary.GeneroResumen;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Obra;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GeneroMapper {

    Genero toEntity(GeneroRequest request);

    void updateEntity(GeneroRequest request, @MappingTarget Genero genero);

    GeneroResumen toResumen(Genero genero);

    GeneroDetalleResponse toDetalleResponse(Genero genero);

    default ObraResumen mapObraToObraResumen(Obra obra) {
        if (obra == null) {
            return null;
        }
        return new ObraResumen(obra.getId(), obra.getTitulo(), obra.getAnioCreacion(), obra.getResumen(), obra.isBorradoLogico());
    }

}
