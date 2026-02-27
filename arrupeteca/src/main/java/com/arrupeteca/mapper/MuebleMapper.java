package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Salon;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "string", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MuebleMapper {

    Mueble toEntity(MuebleRequest request);

    void updateEntity(MuebleRequest request, @MappingTarget Mueble mueble);

}
