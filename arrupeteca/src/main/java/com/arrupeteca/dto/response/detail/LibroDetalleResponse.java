package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.EditorialResponse;
import com.arrupeteca.dto.response.FormatoResponse;
import com.arrupeteca.dto.response.IdiomaResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record LibroDetalleResponse(
    Long id,
    String isbn,
    Integer anioPublicacion,
    BigDecimal precio,
    String urlImagen,
    BigDecimal mora,
    Long version,
    boolean borradoLogico,
    String grado,
    String ciclo,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    ObraResumen obra,
    EditorialResponse editorial,
    FormatoResponse formato,
    Set<IdiomaResponse> idiomas
) { }
