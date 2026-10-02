package com.arrupeteca.transferencia.internal.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.transferencia.dto.request.EstadoTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.EstadoTransferenciaResponse;
import com.arrupeteca.transferencia.internal.entity.EstadoTransferencia;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EstadoTransferenciaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    EstadoTransferencia toEntity(EstadoTransferenciaRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EstadoTransferenciaRequest request, @MappingTarget EstadoTransferencia estadoTransferencia);

    EstadoTransferenciaResponse toResponse(EstadoTransferencia estadoTransferencia);
}
