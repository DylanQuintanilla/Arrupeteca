package com.arrupeteca.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "obra")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Obra extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_obra")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El titulo no puede estas vacio")
    @Size(max = 100, message = "El titulo debe tener un maximo de 100 caracteres")
    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(name = "anio_creacion", nullable = true)
    @Max(value = 9999, message = "El año no puede tener más de 4 digitos")
    private Integer anioCreacion;

    @NotBlank(message = "Se debe de agregar un resumen")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String resumen;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @NotEmpty(message = "la obra debe tener genero")
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    @JoinTable(
            name = "obra_genero",
            joinColumns = @JoinColumn(name = "id_obra"),
            inverseJoinColumns = @JoinColumn(name = "id_genero")
    )
    private Set<Genero> generos = new HashSet<>();

    @NotEmpty(message = "Favor de elegir categoria para la obra")
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    @JoinTable(
            name = "obra_categoria",
            joinColumns = @JoinColumn(name = "id_obra"),
            inverseJoinColumns = @JoinColumn(name = "id_categoria")
    )
    private Set<Categoria> categorias = new HashSet<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "obra", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ObraAutor> obraAutores = new HashSet<>();

}
