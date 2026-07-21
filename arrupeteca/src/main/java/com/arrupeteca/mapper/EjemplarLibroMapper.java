package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.entity.Planta;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    config = CentralMapperConfig.class,
    uses = {LibroMapper.class, MuebleMapper.class}
)
public interface EjemplarLibroMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "libro", ignore = true)
    @Mapping(target = "mueble", ignore = true)
    @Mapping(target = "version", ignore = true)
    EjemplarLibro toEntity(EjemplarLibroRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EjemplarLibroRequest request, @MappingTarget EjemplarLibro ejemplarLibro);

    @Mapping(target = "tituloLibro", source = "libro.obra.titulo")
    @Mapping(target = "nombreMueble", source = "mueble.nombre")
    EjemplarLibroResumen toResumen(EjemplarLibro ejemplarLibro);

    @Mapping(target = "ubicacion", expression = "java(concatenarUbicacion(ejemplarLibro.getMueble()))")
    EjemplarLibroDetalleResponse toDetalleResponse(EjemplarLibro ejemplarLibro);

    default String concatenarUbicacion(Mueble mueble) {
        if (mueble == null) {
            return "N/A";
        }
        StringBuilder sb = new StringBuilder(mueble.getNombre());
        Salon salon = mueble.getSalon();
        if (salon != null) {
            sb.append(" - ").append(salon.getNombre());
            Planta planta = salon.getPlanta();
            if (planta != null) {
                sb.append(" - ").append(planta.getNombre());
            }
        }
        return sb.toString();
    }
}
