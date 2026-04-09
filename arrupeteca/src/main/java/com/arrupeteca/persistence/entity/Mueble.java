package com.arrupeteca.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

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

