package com.arrupeteca.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "salon", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_salon_nombre_planta",
                columnNames = {"nombre", "id_planta"}
        )
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salon")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El nombre del salon no puede estar vacío")
    @Size(max = 25, message = "El nombre no puede exceder 25 caracteres")
    @Column(length = 25, nullable = false)
    private String nombre;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @NotNull
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planta", nullable = false)
    private Planta planta;

}
