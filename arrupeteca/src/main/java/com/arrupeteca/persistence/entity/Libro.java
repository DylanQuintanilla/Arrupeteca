package com.arrupeteca.persistence.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "libro")
@SQLDelete(sql = "UPDATE libro SET borrado_logico = 1 WHERE id_libro = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Libro extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(length = 20, unique = true)
    @ToString.Include
    private String isbn;

    @Column(name = "anio_publicacion", nullable = false)
    @ToString.Include
    private Integer anioPublicacion;

    @Column(precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal precio = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT", name = "url_imagen")
    private String urlImagen;

    @Column(precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal mora = BigDecimal.ZERO;

    @Column(nullable = false, name = "borrado_logico", columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    @ToString.Include
    private boolean borradoLogico = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grado")
    private Grado grado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo")
    private Ciclo ciclo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_obra", nullable = false)
    private Obra obra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_editorial", nullable = false)
    private Editorial editorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formato", nullable = false)
    private Formato formato;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "libro_idioma",
            joinColumns = @JoinColumn(name = "id_libro"),
            inverseJoinColumns = @JoinColumn(name = "id_idioma")
    )
    private Set<Idioma> idiomas = new HashSet<>();

}
