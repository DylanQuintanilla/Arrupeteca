package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.persistence.entity.Planta;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "string", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlantaMapper {

    Planta toEntity(PlantaRequest request);

    void updateEntity(PlantaRequest request, @MappingTarget Planta planta);

}
