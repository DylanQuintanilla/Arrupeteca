package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.persistence.entity.Pais;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaisMapper {

    Pais toEntity(PaisRequest request);

    void updateEntity(PaisRequest request, @MappingTarget Pais pais);

}
