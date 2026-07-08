package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.dto.response.summary.TipoAutoriaResumen;

public record ObraAutorDetalleResponse(
    Long id,
    AutorResumenResponse autor,
    TipoAutoriaResumen tipoAutoria
) { }
