package com.arrupeteca.dto.request;

import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.ISBN;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LibroRequest {

    @ISBN(message = "El ISBN no es válido")
    @Size(max = 20, message = "El ISBN no puede contener más de 20 dígitos")
    private String isbn;

    @NotNull(message = "El año de publicación no puede estar vacío")
    @Max(value = 9999, message = "El año de publicación no puede tener más de 4 dígitos")
    private Integer anioPublicacion;

    private BigDecimal precio;

    private String urlImagen;

    private BigDecimal mora;

    private Grado grado;

    private Ciclo ciclo;


    @NotNull(message = "Debe elegir una obra para el libro")
    private Long idObra;

    @NotNull(message = "El libro debe pertenecer a un formato")
    private Long idFormato;

    @NotNull(message = "El libro debe pertenecer a una editorial")
    private Long idEditorial;

    @NotEmpty(message = "Debe elegir al menos un idioma para el libro")
    private Set<Long> idIdiomas;

}
