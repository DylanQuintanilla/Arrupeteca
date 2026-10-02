package com.arrupeteca.transferencia.internal.mapper;

import com.arrupeteca.mapper.CentralMapperConfig;
import com.arrupeteca.transferencia.dto.request.EjemplarTransferenciaRequest;
import com.arrupeteca.transferencia.dto.request.TransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.EjemplarTransferenciaResponse;
import com.arrupeteca.transferencia.dto.response.TransferenciaDetalleResponse;
import com.arrupeteca.transferencia.internal.entity.EjemplarTransferencia;
import com.arrupeteca.transferencia.internal.entity.Transferencia;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(config = CentralMapperConfig.class)
public interface TransferenciaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "solicitudTransferencia", ignore = true)
    @Mapping(target = "usuarioEnvia", ignore = true)
    @Mapping(target = "usuarioRecibe", ignore = true)
    @Mapping(target = "muebleOrigen", ignore = true)
    @Mapping(target = "muebleDestino", ignore = true)
    @Mapping(target = "estadoTransferencia", ignore = true)
    @Mapping(target = "ejemplares", ignore = true)
    Transferencia toEntity(TransferenciaRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(TransferenciaRequest request, @MappingTarget Transferencia transferencia);

    @Mapping(target = "idSolicitudTransferencia", source = "solicitudTransferencia.id")
    @Mapping(target = "idUsuarioEnvia", source = "usuarioEnvia.id")
    @Mapping(target = "nombreUsuarioEnvia", source = "usuarioEnvia.nombres")
    @Mapping(target = "idUsuarioRecibe", source = "usuarioRecibe.id")
    @Mapping(target = "nombreUsuarioRecibe", source = "usuarioRecibe.nombres")
    @Mapping(target = "idMuebleOrigen", source = "muebleOrigen.id")
    @Mapping(target = "nombreMuebleOrigen", source = "muebleOrigen.nombre")
    @Mapping(target = "idMuebleDestino", source = "muebleDestino.id")
    @Mapping(target = "nombreMuebleDestino", source = "muebleDestino.nombre")
    @Mapping(target = "idEstadoTransferencia", source = "estadoTransferencia.id")
    @Mapping(target = "nombreEstadoTransferencia", source = "estadoTransferencia.nombre")
    @Mapping(target = "ejemplares", source = "ejemplares")
    TransferenciaDetalleResponse toDetalleResponse(Transferencia transferencia);

    @Mapping(target = "idEjemplarLibro", source = "ejemplarLibro.id")
    @Mapping(target = "tituloObra", source = "ejemplarLibro.libro.obra.titulo")
    @Mapping(target = "idEstadoFisicoAlMomento", source = "estadoFisicoAlMomento.id")
    @Mapping(target = "estadoFisicoAlMomento", source = "estadoFisicoAlMomento.nombre")
    EjemplarTransferenciaResponse toEjemplarItemResponse(EjemplarTransferencia ejemplar);

    List<EjemplarTransferenciaResponse> toEjemplarItemResponseList(Set<EjemplarTransferencia> ejemplares);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transferencia", ignore = true)
    @Mapping(target = "ejemplarLibro", ignore = true)
    @Mapping(target = "estadoFisicoAlMomento", ignore = true)
    EjemplarTransferencia toEjemplarEntity(EjemplarTransferenciaRequest request);
}
