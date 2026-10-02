package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.ResponsableRequest;
import com.arrupeteca.dto.response.ResponsableResponse;
import com.arrupeteca.persistence.entity.Responsable;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface ResponsableMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "parentesco", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    Responsable toEntity(ResponsableRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ResponsableRequest request, @MappingTarget Responsable responsable);

    @Mapping(target = "idUsuario", source = "usuario.id")
    @Mapping(target = "nombreUsuario",
            expression = "java(responsable.getUsuario() != null ? "
                    + "responsable.getUsuario().getNombres() + \" \" + responsable.getUsuario().getApellidos() : null)")
    @Mapping(target = "idParentesco", source = "parentesco.id")
    @Mapping(target = "nombreParentesco", source = "parentesco.nombre")
    ResponsableResponse toResponse(Responsable responsable);

}
