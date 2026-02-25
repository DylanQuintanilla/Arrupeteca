package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.persistence.entity.TipoAutoria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TipoAutoriaMapper {

    TipoAutoria toEntity(TipoAutoriaRequest request);

    void updateEntity(TipoAutoriaRequest request, @MappingTarget TipoAutoria TipoAutoria);

}