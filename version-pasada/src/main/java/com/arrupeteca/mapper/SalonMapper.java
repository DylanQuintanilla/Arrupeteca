package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Salon;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalonMapper {

    Salon toEntity(SalonRequest request);

    void updateEntity(SalonRequest request, @MappingTarget Salon salon);

}
