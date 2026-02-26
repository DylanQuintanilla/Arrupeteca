package com.arrupeteca.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "obra_autor", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_obra_autor_rol",
                columnNames = {"id_obra", "id_autor", "id_tipo_autoria"}
        )
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ObraAutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "id_autor", nullable = false)
    private Autor autor;

    @NotNull
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_obra", nullable = false)
    private Obra obra;

    @NotNull
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_autoria", nullable = false)
    private TipoAutoria tipoAutoria;

    @Column(nullable = false, name = "borrado_logico", columnDefinition = "TINYINT(1) DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;
}