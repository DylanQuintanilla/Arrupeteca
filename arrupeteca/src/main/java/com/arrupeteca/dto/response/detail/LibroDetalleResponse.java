package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.EditorialResumen;
import com.arrupeteca.dto.response.summary.FormatoResumen;
import com.arrupeteca.dto.response.summary.IdiomaResumen;
import com.arrupeteca.dto.response.summary.ObraResumen;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record LibroDetalleResponse(
    Long id,
    String isbn,
    Integer anioPublicacion,
    BigDecimal precio,
    String urlImagen,
    BigDecimal mora,
    boolean borradoLogico,
    String grado,
    String ciclo,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    ObraResumen obra,
    EditorialResumen editorial,
    FormatoResumen formato,
    Set<IdiomaResumen> idiomas
) { }
