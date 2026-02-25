package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.projection.GeneroResumen;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GeneroMapper {

    Genero toEntity(GeneroRequest request);

    void updateEntity(GeneroRequest request, @MappingTarget Genero genero);

}
