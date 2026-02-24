package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.persistence.entity.Nacionalidad;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NacionalidadMapper {

    Nacionalidad toEntity(NacionalidadRequest request);

    void updateEntity(NacionalidadRequest request, @MappingTarget Nacionalidad nacionalidad);

}
