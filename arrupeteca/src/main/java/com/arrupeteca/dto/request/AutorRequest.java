package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder //Tests
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutorRequest {

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 50, message = "El seudonimo no puede exceder 50 caracteres")
    private String seudonimo;

    @Max(value = 9999, message = "El año de nacimiento no puede tener más de 4 digitos")
    private Integer anioNacimiento;

    @Max(value = 9999, message = "El año de fallecimiento no puede tener más de 4 digitos")
    private Integer anioFallecimiento;

    @NotBlank(message = "La biografia no puede estar vacía")
    @Size(max = 5000, message = "La biografia es demasiado larga")
    private String biografia;

    private Long idPaisNacimiento;
    private Long idNacionalidad;
    private Long idGeneroPrincipal;
    private Long idObraCumbre;

    @AssertTrue(message = "El autor debe tener al menos un Nombre o un Seudonimo")
    public boolean isIdentidadValida() {
        boolean tieneNombreCompleto = (nombre != null && !nombre.isBlank());
        boolean tieneSeudonimo = (seudonimo != null && !seudonimo.isBlank());
        return tieneNombreCompleto || tieneSeudonimo;
    }

    @AssertTrue(message = "El año de nacimiento no puede ser mayor al año de fallecimiento")
    public boolean isFechasValidas() {
        if (anioNacimiento == null || anioFallecimiento == null) {
            return true;
        }
        return anioNacimiento <= anioFallecimiento;
    }

}
