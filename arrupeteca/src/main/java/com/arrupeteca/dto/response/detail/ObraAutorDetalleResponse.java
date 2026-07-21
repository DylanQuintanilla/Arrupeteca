package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.TipoAutoriaResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;

public record ObraAutorDetalleResponse(
    Long id,
    AutorResumenResponse autor,
    TipoAutoriaResponse tipoAutoria
) { }
