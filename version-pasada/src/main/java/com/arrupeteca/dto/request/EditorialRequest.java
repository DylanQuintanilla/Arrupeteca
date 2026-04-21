package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class EditorialRequest {

    @NotBlank(message = "El nombre de la editorial no puede estar vacia")
    @Size(max = 100, message = "El nombre de la editorial no debe exceder los 100 caracteres")
    private String nombre;

    @Size(max = 150, message = "La direccion de la editorial no debe exceder los 150 caracteres")
    private String direccion;

    @NotBlank(message = "El correo de la editorial no puede estar vacia")
    @Size(max = 100, message = "El correo de la editorial no debe exceder los 100 caracteres")
    @Email(message = "El formato del correo es inválido")
    private String correo;

    @NotBlank(message = "El numero telefonico de la editorial no puede estar vacio")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "El teléfono debe tener el formato 1234-5678")
    private String telefono;

    @NotBlank(message = "El nombre de la persona de contacto no puede estar vacia")
    @Size(max = 100, message = "El nombre de la persona de contacto no debe exceder los 100 caracteres")
    private String personaContacto;

}
