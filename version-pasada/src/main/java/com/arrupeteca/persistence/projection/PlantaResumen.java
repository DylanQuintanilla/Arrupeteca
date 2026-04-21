package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

public interface PlantaResumen {

    Long getId();
    String getNombre();
    boolean getBorradoLogico();

    @Value("#{target.edificio?.nombre}")
    String getNombreEdificio();

}
