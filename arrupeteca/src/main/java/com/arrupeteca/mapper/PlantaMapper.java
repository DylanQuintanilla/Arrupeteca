package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.detail.PlantaDetalleResponse;
import com.arrupeteca.dto.response.summary.PlantaResumen;
import com.arrupeteca.persistence.entity.Planta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EdificioMapper.class})
public interface PlantaMapper {

    Planta toEntity(PlantaRequest request);

    void updateEntity(PlantaRequest request, @MappingTarget Planta planta);

    @Mapping(target = "nombreEdificio", source = "edificio.nombre")
    PlantaResumen toResumen(Planta planta);

    PlantaDetalleResponse toDetalleResponse(Planta planta);

}
