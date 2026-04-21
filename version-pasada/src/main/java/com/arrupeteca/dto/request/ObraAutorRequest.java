package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObraAutorRequest {

    @NotNull(message = "El ID del autor es obligatorio")
    private Long idAutor;

    @NotNull(message = "El ID del tipo de autoría es obligatorio")
    private Long idTipoAutoria;

}