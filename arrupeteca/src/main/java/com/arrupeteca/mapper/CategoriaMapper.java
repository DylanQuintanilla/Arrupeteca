package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.persistence.entity.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoriaMapper {

    Categoria toEntity(CategoriaRequest request);

    void updateEntity(CategoriaRequest request, @MappingTarget Categoria categoria);

}
