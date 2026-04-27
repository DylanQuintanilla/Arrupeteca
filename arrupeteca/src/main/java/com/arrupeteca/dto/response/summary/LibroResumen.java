package com.arrupeteca.dto.response.summary;

import java.math.BigDecimal;

public record LibroResumen(
    Long id,
    String isbn,
    Integer anioPublicacion,
    BigDecimal precio,
    String urlImagen,
    BigDecimal mora,
    boolean borradoLogico,
    String grado,
    String ciclo,
    String tituloObra,
    String nombreEditorial,
    String nombreFormato
) { }
