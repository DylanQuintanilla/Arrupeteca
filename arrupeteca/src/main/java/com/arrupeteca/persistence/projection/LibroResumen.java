package com.arrupeteca.persistence.projection;


import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.util.List;

public interface LibroResumen {

    Long getId();
    String getIsbn();
    Integer getAnioPublicacion();
    BigDecimal getPrecio();
    String getUrlImagen();
    BigDecimal getMora();
    boolean getBorradoLogico();

    @Value("#{target.grado?.nombre}")
    String getNombreGrado();

    @Value("#{target.ciclo?.nombre}")
    String getNombreCiclo();

    @Value("#{target.obra?.titulo}")
    String getTituloObra();

    @Value("#{target.obra?.anioCreacion}")
    Integer getAnioCreacionObra();

    @Value("#{target.obra?.resumen}")
    String getResumenObra();

    @Value("#{target.obra?.generos?.![nombre]}")
    List<String> getGeneroObra();

    @Value("#{target.obra?.categorias?.![nombre]}")
    List<String> getCategoriaObra();

    @Value("#{target.obra?.obraAutores?.![ (tipoAutoria?.nombre ?: 'N/A') + ': ' + (autor?.nombre ?: autor?.seudonimo ?: 'Desconocido') ]}")
    List<String> getDetalleAutores();

    @Value("#{target.editorial?.nombre}")
    String getNombreEditorial();

    @Value("#{target.formato?.nombre}")
    String getNombreFormato();

    @Value("#{target.idiomas?.![nombre]}")
    List<String> getListaIdiomas();

}
