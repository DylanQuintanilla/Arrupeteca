package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.TipoUsuarioRequest;
import com.arrupeteca.dto.response.TipoUsuarioResponse;
import com.arrupeteca.persistence.entity.TipoUsuario;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface TipoUsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    TipoUsuario toEntity(TipoUsuarioRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(TipoUsuarioRequest request, @MappingTarget TipoUsuario tipoUsuario);

    TipoUsuarioResponse toResponse(TipoUsuario tipoUsuario);

}
