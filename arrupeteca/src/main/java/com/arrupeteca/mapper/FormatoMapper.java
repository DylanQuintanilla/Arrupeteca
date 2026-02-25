package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.persistence.entity.Formato;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FormatoMapper {

    Formato toEntity(FormatoRequest request);

    void updateEntity(FormatoRequest request, @MappingTarget Formato Formato);

}