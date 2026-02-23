package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

public interface AutorResumen {

    Long getId();
    String getNombre();
    String getSeudonimo();
    Integer getAnioNacimiento();
    Integer getAnioFallecimiento();
    String getBiografia();
    boolean getBorradoLogico();

    @Value("#{target.paisNacimiento?.nombre}")
    String getNombrePais();

    @Value("#{target.nacionalidad?.nombre}")
    String getNombreNacionalidad();

    @Value("#{target.generoPrincipal?.nombre}")
    String getNombreGeneroPrincipal();

    @Value("#{target.obraCumbre?.titulo}")
    String getNombreObraCumbre();

}