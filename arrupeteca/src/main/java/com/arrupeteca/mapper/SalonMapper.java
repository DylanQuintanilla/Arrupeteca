package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.SalonResponse;
import com.arrupeteca.persistence.entity.Salon;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class, uses = {PlantaMapper.class})
public interface SalonMapper {

    Salon toEntity(SalonRequest request);

    void updateEntity(SalonRequest request, @MappingTarget Salon salon);

    SalonResponse toResponse(Salon salon);
}
