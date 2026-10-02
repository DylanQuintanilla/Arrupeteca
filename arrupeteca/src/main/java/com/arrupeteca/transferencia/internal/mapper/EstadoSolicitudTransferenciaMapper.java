package com.arrupeteca.transferencia.internal.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.transferencia.dto.request.EstadoSolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.EstadoSolicitudTransferenciaResponse;
import com.arrupeteca.transferencia.internal.entity.EstadoSolicitudTransferencia;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EstadoSolicitudTransferenciaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    EstadoSolicitudTransferencia toEntity(EstadoSolicitudTransferenciaRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EstadoSolicitudTransferenciaRequest request, @MappingTarget EstadoSolicitudTransferencia estadoSolicitudTransferencia);

    EstadoSolicitudTransferenciaResponse toResponse(EstadoSolicitudTransferencia estadoSolicitudTransferencia);
}
