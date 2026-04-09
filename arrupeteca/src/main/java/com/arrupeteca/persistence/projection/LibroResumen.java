package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

public interface LibroResumen {

    Long getId();

    String getIsbn();

    Integer getAnioPublicacion();

    Integer getEdicion();

    boolean getBorradoLogico();

    @Value("#{target.obra?.titulo}")
    String getTituloObra();

    @Value("#{target.editorial?.nombre}")
    String getNombreEditorial();

    @Value("#{target.formato?.nombre}")
    String getNombreFormato();

}
