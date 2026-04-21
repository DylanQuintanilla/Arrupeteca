package com.arrupeteca.dto.request;

import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EjemplarLibroRequest {

    @Size(message = "El comentario no puede exceder los 255 caracteres")
    private String comentario;

    private EstadoFisico estadoFisico;

    private Disponibilidad disponibilidad;

    @NotNull(message = "Se debe elegir un libro")
    private Long idLibro;

    @NotNull(message = "Por favor ingresar donde se econtrara el ejemplar")
    private Long idMueble;

}
