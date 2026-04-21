package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

public interface MuebleResumen {

    Long getId();
    String getNombre();
    boolean getBorradoLogico();

    @Value("#{ (target.salon?.nombre ?: '') + ' - ' + (target.salon?.planta?.nombre ?: '') + ' - ' + (target.salon?.planta?.edificio?.nombre ?: '') }")
    String getUbicacionSalon();
}
