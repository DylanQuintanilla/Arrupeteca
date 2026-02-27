package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EjemplarLibroMapper {

    EjemplarLibro toEntity(EjemplarLibroRequest request);

    void updateEntity(EjemplarLibroRequest request, @MappingTarget EjemplarLibro ejemplarLibro);

}
