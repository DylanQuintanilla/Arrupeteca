package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObraRequest {

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 100, message = "El título debe tener un máximo de 100 caracteres")
    private String titulo;

    @Max(value = 9999, message = "El año no puede tener más de 4 dígitos")
    private Integer anioCreacion;

    @NotBlank(message = "Se debe de agregar un resumen")
    private String resumen;

    @NotEmpty(message = "La obra debe tener al menos un género")
    private Set<Long> idGeneros;

    @NotEmpty(message = "Favor de elegir al menos una categoría para la obra")
    private Set<Long> idCategorias;

    @NotEmpty(message = "La obra debe tener al menos un autor asociado")
    @Valid
    private Set<ObraAutorRequest> autores;

}