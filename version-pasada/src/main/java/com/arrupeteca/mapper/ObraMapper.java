package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.persistence.entity.Obra;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ObraMapper {

    Obra toEntity(ObraRequest request);

    void updateEntity(ObraRequest request, @MappingTarget Obra obra);

}