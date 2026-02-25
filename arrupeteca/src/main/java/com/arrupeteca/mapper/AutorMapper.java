package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.persistence.entity.Autor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AutorMapper {

    Autor toEntity(AutorRequest request);

    void updateEntity(AutorRequest request, @MappingTarget Autor autor);

}
