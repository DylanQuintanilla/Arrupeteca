package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.detail.MuebleDetalleResponse;
import com.arrupeteca.dto.response.summary.MuebleResumen;
import com.arrupeteca.persistence.entity.Mueble;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {SalonMapper.class})
public interface MuebleMapper {

    Mueble toEntity(MuebleRequest request);

    void updateEntity(MuebleRequest request, @MappingTarget Mueble mueble);

    @Mapping(target = "nombreSalon", source = "salon.nombre")
    MuebleResumen toResumen(Mueble mueble);

    MuebleDetalleResponse toDetalleResponse(Mueble mueble);

}
