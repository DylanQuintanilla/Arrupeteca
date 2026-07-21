package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.persistence.entity.Mueble;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class, uses = {SalonMapper.class})
public interface MuebleMapper {

    Mueble toEntity(MuebleRequest request);

    void updateEntity(MuebleRequest request, @MappingTarget Mueble mueble);

    MuebleResponse toResponse(Mueble mueble);
}
