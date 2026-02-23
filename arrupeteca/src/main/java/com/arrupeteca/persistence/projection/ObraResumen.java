package com.arrupeteca.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface ObraResumen {

    Long getId();
    String getTitulo();
    Integer getAnioCreacion();
    String getResumen();
    boolean getBorradoLogico();

    @Value("#{target.generos?.![nombre]}")
    List<String> getListaGeneros();

    @Value("#{target.categorias?.![nombre]}")
    List<String> getListaCategorias();

    @Value("#{target.obraAutores?.![ (tipoAutoria?.nombre ?: 'N/A') + ': ' + (autor?.nombre ?: autor?.seudonimo ?: 'Desconocido') ]}")
    List<String> getDetalleAutores();
}
