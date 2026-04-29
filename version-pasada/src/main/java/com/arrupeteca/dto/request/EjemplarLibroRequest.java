package com.arrupeteca.dto.request;

import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EjemplarLibroRequest {

    @Size(max = 255, message = "El comentario no puede exceder los 255 caracteres")
    private String comentario;

    private EstadoFisico estadoFisico;

    private Disponibilidad disponibilidad;

    @NotNull(message = "Se debe elegir un libro")
    private Long idLibro;

    @NotNull(message = "Por favor ingresar donde se econtrara el ejemplar")
    private Long idMueble;

}
