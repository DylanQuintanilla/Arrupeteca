package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

public interface ObraResumen {

    Long getId();

    String getTitulo();

    Integer getAnioCreacion();

    String getResumen();

    boolean getBorradoLogico();

}
