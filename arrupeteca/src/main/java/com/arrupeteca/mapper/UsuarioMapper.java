package com.arrupeteca.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.dto.request.ActualizarPerfilRequest;
import com.arrupeteca.dto.request.UsuarioAdminRequest;
import com.arrupeteca.dto.response.detail.UsuarioDetalleResponse;
import com.arrupeteca.dto.response.summary.UsuarioResumen;
import com.arrupeteca.persistence.entity.Usuario;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(
    config = CentralMapperConfig.class,
    uses = {TipoUsuarioMapper.class}
)
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "bloqueadoPorMora", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "ultimaFechaActividad", ignore = true)
    @Mapping(target = "tipoUsuario", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    Usuario toEntity(UsuarioAdminRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(UsuarioAdminRequest request, @MappingTarget Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "correo", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "bloqueadoPorMora", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "ultimaFechaActividad", ignore = true)
    @Mapping(target = "tipoUsuario", ignore = true)
    void updatePerfil(ActualizarPerfilRequest request, @MappingTarget Usuario usuario);

    @Named("toResumen")
    @Mapping(target = "nombreTipoUsuario", source = "tipoUsuario.nombre")
    UsuarioResumen toResumen(Usuario usuario);

    UsuarioDetalleResponse toDetalleResponse(Usuario usuario);
}
