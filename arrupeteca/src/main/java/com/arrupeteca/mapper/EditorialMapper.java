package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.persistence.entity.Editorial;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EditorialMapper {

    Editorial toEntity(EditorialRequest request);

    void updateEntity(EditorialRequest request, @MappingTarget Editorial editorial);

}
