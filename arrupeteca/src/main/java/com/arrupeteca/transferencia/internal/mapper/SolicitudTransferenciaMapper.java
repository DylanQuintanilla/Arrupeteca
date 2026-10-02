package com.arrupeteca.transferencia.internal.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.transferencia.dto.request.DetalleSolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.request.SolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.DetalleSolicitudTransferenciaResponse;
import com.arrupeteca.transferencia.dto.response.SolicitudTransferenciaDetalleResponse;
import com.arrupeteca.transferencia.internal.entity.DetalleSolicitudTransferencia;
import com.arrupeteca.transferencia.internal.entity.SolicitudTransferencia;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(config = CentralMapperConfig.class)
public interface SolicitudTransferenciaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "salonDestino", ignore = true)
    @Mapping(target = "estadoSolicitudTransferencia", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    SolicitudTransferencia toEntity(SolicitudTransferenciaRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(SolicitudTransferenciaRequest request, @MappingTarget SolicitudTransferencia solicitudTransferencia);

    @Mapping(target = "idUsuario", source = "usuario.id")
    @Mapping(target = "nombreUsuario", source = "usuario.nombres")
    @Mapping(target = "idSalonDestino", source = "salonDestino.id")
    @Mapping(target = "nombreSalonDestino", source = "salonDestino.nombre")
    @Mapping(target = "idEstadoSolicitudTransferencia", source = "estadoSolicitudTransferencia.id")
    @Mapping(target = "nombreEstadoSolicitudTransferencia", source = "estadoSolicitudTransferencia.nombre")
    @Mapping(target = "detalles", source = "detalles")
    SolicitudTransferenciaDetalleResponse toDetalleResponse(SolicitudTransferencia solicitudTransferencia);

    @Mapping(target = "idLibro", source = "libro.id")
    @Mapping(target = "tituloLibro", source = "libro.obra.titulo")
    DetalleSolicitudTransferenciaResponse toDetalleItemResponse(DetalleSolicitudTransferencia detalle);

    List<DetalleSolicitudTransferenciaResponse> toDetalleItemResponseList(Set<DetalleSolicitudTransferencia> detalles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "solicitudTransferencia", ignore = true)
    @Mapping(target = "libro", ignore = true)
    DetalleSolicitudTransferencia toDetalleEntity(DetalleSolicitudTransferenciaRequest request);
}
