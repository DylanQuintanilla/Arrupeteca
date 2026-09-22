package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.persistence.entity.Planta;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class, uses = {EdificioMapper.class})
public interface PlantaMapper {

    Planta toEntity(PlantaRequest request);

    void updateEntity(PlantaRequest request, @MappingTarget Planta planta);

    PlantaResponse toResponse(Planta planta);
}
