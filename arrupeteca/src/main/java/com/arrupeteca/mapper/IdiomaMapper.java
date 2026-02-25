package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.persistence.entity.Idioma;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IdiomaMapper {

    Idioma toEntity(IdiomaRequest request);

    void updateEntity(IdiomaRequest request, @MappingTarget Idioma Idioma);

}