package com.arrupeteca.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "editorial")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Editorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_editorial")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El nombre de la editorial no puede estar vacia")
    @Size(max = 100, message = "El nombre de la editorial no debe exceder los 100 caracteres")
    @Column(length = 100, nullable = false, unique = true)
    private String nombre;

    @Size(max = 150, message = "La direccion de la editorial no debe exceder los 150 caracteres")
    @Column(length = 150)
    private String direccion;

    @NotBlank(message = "El correo de la editorial no puede estar vacia")
    @Size(max = 100, message = "El correo de la editorial no debe exceder los 100 caracteres")
    @Email(message = "El formato del correo es inválido")
    @Column(length = 100, nullable = false, unique = true)
    private String correo;

    @NotBlank(message = "El numero telefonico de la editorial no puede estar vacio")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "El teléfono debe tener el formato 1234-5678")
    @Column(length = 9, nullable = false)
    private String telefono;

    @NotBlank(message = "El nombre de la persona de contacto no puede estar vacia")
    @Size(max = 100, message = "El nombre de la persona de contacto no debe exceder los 100 caracteres")
    @Column(name = "persona_contacto", nullable = false, length = 100)
    private String personaContacto;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;
}