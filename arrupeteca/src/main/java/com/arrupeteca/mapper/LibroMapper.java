package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.persistence.entity.Libro;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LibroMapper {

    Libro toEntity(LibroRequest request);

    void updateEntity(LibroRequest request, @MappingTarget Libro libro);

}
