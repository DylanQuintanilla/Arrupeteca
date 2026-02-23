package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

public interface EjemplarLibroResumen {

    Long getId();
    String getComentario();
    boolean getBorradoLogico();

    @Value("#{target.libro?.anioPublicacion}")
    Integer getAnioPublicacionLibro();

    @Value("#{target.libro?.urlImagen}")
    String getUrlImagenLibro();

    @Value("#{target.libro?.obra?.titulo}")
    String getTituloObra();

    @Value("#{target.libro?.editorial?.nombre}")
    String getEditorialLibro();

    @Value("#{ (target.mueble?.nombre ?: '') + ' - ' + (target.mueble?.salon?.nombre ?: '') + ' - ' + (target.mueble?.salon?.planta?.nombre ?: '') + ' - ' + (target.mueble?.salon?.planta?.edificio?.nombre ?: '') }")
    String getUbicacionEjemplar();

    @Value("#{target.estadoFisico?.nombre}")
    String getNombreEstadoFisico();

    @Value("#{target.disponibilidad?.nombre}")
    String getNombreDisponibilidad();

}
