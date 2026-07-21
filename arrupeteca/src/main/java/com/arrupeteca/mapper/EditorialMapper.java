package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.EditorialResponse;
import com.arrupeteca.persistence.entity.Editorial;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EditorialMapper {

    Editorial toEntity(EditorialRequest request);

    void updateEntity(EditorialRequest request, @MappingTarget Editorial editorial);

    EditorialResponse toResponse(Editorial editorial);
}
