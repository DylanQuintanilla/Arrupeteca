package com.arrupeteca.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "idioma")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Idioma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_idioma")
    @EqualsAndHashCode.Include
    private Long id;

    @Size(max = 50, message = "El nombre no puede ser mayor de 50 caracteres")
    @NotBlank(message = "El nombre no puede estar vacio")
    @Column(length = 50, unique = true, nullable = false)
    private String nombre;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @ToString.Exclude
    @ManyToMany(mappedBy = "idiomas", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Libro> libros = new HashSet<>();

}
