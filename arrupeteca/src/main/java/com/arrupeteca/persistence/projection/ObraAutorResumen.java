package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

public interface ObraAutorResumen {

    Long getId();

    boolean getBorradoLogico();

    @Value("#{target.autor?.id}")
    Long getAutorId();

    @Value("#{target.autor?.nombre ?: target.autor?.seudonimo ?: 'Desconocido'}")
    String getAutorNombre();

    @Value("#{target.tipoAutoria?.id}")
    Long getTipoAutoriaId();

    @Value("#{target.tipoAutoria?.nombre ?: 'Sin Rol'}")
    String getTipoAutoriaNombre();
}
