package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

public interface EjemplarLibroResumen {

    Long getId();

    String getCodigoInventario();

    String getEstadoFisico();

    String getDisponibilidad();

    boolean getBorradoLogico();

    @Value("#{target.libro?.titulo}")
    String getTituloLibro();

    @Value("#{target.mueble?.nombre}")
    String getNombreMueble();

}
