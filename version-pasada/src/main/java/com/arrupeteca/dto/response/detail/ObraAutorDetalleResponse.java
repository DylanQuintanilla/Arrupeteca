package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.AutorResumen;
import com.arrupeteca.dto.response.summary.TipoAutoriaResumen;

public record ObraAutorDetalleResponse(
    Long id,
    AutorResumen autor,
    TipoAutoriaResumen tipoAutoria
) { }
