package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

public interface SalonResumen {

    Long getId();
    String getNombre();
    boolean getBorradoLogico();

    @Value("#{ (target.planta?.nombre ?: '') + ' - ' + (target.planta?.edificio?.nombre ?: '') }")
    String getUbicacionPlanta();

}
