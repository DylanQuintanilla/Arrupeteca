package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.CicloRequest;
import com.arrupeteca.dto.response.CicloResponse;
import com.arrupeteca.persistence.entity.Ciclo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface CicloMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    Ciclo toEntity(CicloRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(CicloRequest request, @MappingTarget Ciclo ciclo);

    CicloResponse toResponse(Ciclo ciclo);
}
