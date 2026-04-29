package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.detail.SalonDetalleResponse;
import com.arrupeteca.dto.response.summary.SalonResumen;
import com.arrupeteca.persistence.entity.Salon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {PlantaMapper.class})
public interface SalonMapper {

    Salon toEntity(SalonRequest request);

    void updateEntity(SalonRequest request, @MappingTarget Salon salon);

    @Mapping(target = "nombrePlanta", source = "planta.nombre")
    SalonResumen toResumen(Salon salon);

    SalonDetalleResponse toDetalleResponse(Salon salon);

}
