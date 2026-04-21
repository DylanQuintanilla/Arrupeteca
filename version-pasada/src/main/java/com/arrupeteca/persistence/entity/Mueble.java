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
@Table(name = "mueble", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_mueble_nombre_salon",
                columnNames = {"nombre", "id_salon"}
        )
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Mueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mueble")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El nombre del mueble no puede estar vacío")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @NotNull(message = "Favor de elegir el salon")
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_salon", nullable = false)
    private Salon salon;

}
