package com.arrupeteca.persistence.entity;

import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.ISBN;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "libro")
public class Libro extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Long id;

    @ISBN(message = "El ISBN no es valdio")
    @Size(max = 20, message = "El ISBN no puede contener mas de 20 digitos")
    @Column(length = 20, unique = true)
    private String isbn;

    @NotNull(message = "El valor de publicacion no puede estar vacio")
    @Column(name = "anio_publicacion", nullable = false)
    @Max(value = 9999, message = "El año de publicación no puede tener más de 4 dígitos")
    private Integer anioPublicacion;

    @Column(precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal precio = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT", name = "url_imagen")
    private String urlImagen;

    @Column(precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal mora = BigDecimal.ZERO;

    @Column(nullable = false, name = "borrado_logico", columnDefinition = "TINYINT(1) DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "grado", nullable = true)
    private Grado grado;

    @Enumerated(EnumType.STRING)
    @Column(name = "ciclo", nullable = true)
    private Ciclo ciclo;

    @NotNull(message = "Debe elegir una obra")
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_obra", nullable = false)
    private Obra obra;

    @NotNull(message = "El libro debe pertenecer a una editorial")
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_editorial", nullable = false)
    private Editorial editorial;

    @ToString.Exclude
    @NotNull(message = "El libro debe tener un formato")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formato", nullable = false)
    private Formato formato;

    @NotEmpty
    @ToString.Exclude
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "libro_idioma",
            joinColumns = @JoinColumn(name = "id_libro"),
            inverseJoinColumns = @JoinColumn(name = "id_idioma")
    )
    private List<Idioma> idiomas = new ArrayList<>();


}