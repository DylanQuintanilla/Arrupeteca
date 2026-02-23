package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.persistence.entity.Autor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * componentModel = "spring": Le dice a MapStruct que convierta esto en un @Component
 * para poder inyectarlo en el Service con @RequiredArgsConstructor.
 * * unmappedTargetPolicy = ReportingPolicy.IGNORE: Le dice a MapStruct que no lance
 * advertencias rojas por los campos que no mapeamos directamente (como el ID,
 * la Nacionalidad, la Auditoría, etc.), ya que esos los manejamos en el Service.
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AutorMapper {

    Autor toEntity(AutorRequest request);

    void updateEntity(AutorRequest request, @MappingTarget Autor autor);

}
