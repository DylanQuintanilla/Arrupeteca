package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.detail.EdificioDetalleResponse;
import com.arrupeteca.dto.response.summary.EdificioResumen;
import com.arrupeteca.persistence.entity.Edificio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EdificioMapper {

    Edificio toEntity(EdificioRequest request);

    void updateEntity(EdificioRequest request, @MappingTarget Edificio edificio);

    EdificioResumen toResumen(Edificio edificio);

    EdificioDetalleResponse toDetalleResponse(Edificio edificio);

}
