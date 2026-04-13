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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
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
@Entity
@Builder
@Table(name = "autor")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Autor extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_autor")
    @EqualsAndHashCode.Include
    private Long id;

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(length = 100, nullable = true)
    private String nombre;

    @Size(max = 50, message = "El seudonimo no puede exceder 50 caracteres")
    @Column(length = 50, nullable = true)
    private String seudonimo;

    @Column(name = "anio_nacimiento", nullable = true)
    @Max(value = 9999, message = "El año de nacimiento no puede tener más de 4 digitos")
    private Integer anioNacimiento;

    @Column(name = "anio_fallecimiento", nullable = true)
    @Max(value = 9999, message = "El año de fallecimiento no puede tener más de 4 digitos")
    private Integer anioFallecimiento;

    @NotBlank(message = "La biografia no puede estar vacía")
    @Size(max = 5000, message = "La biografia es demasiado larga")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String biografia;

    @Column(nullable = false, name = "borrado_logico", columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pais_nacimiento", nullable = true)
    private Pais paisNacimiento;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nacionalidad", nullable = true)
    private Nacionalidad nacionalidad;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_genero_principal", nullable = true)
    private Genero generoPrincipal;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_obra", nullable = true)
    private Obra obraCumbre;

    // --- VALIDACIONES ---

    @AssertTrue(message = "El autor debe tener al menos un Nombre o un Seudonimo")
    public boolean isIdentidadValida() {
        boolean tieneNombreCompleto = (nombre != null && !nombre.isBlank());
        boolean tieneSeudonimo = (seudonimo != null && !seudonimo.isBlank());
        return tieneNombreCompleto || tieneSeudonimo;
    }

    @AssertTrue(message = "El año de nacimiento no puede ser mayor al año de fallecimiento")
    public boolean isFechasValidas() {
        if (anioNacimiento == null || anioFallecimiento == null) {
            return true;
        }
        return anioNacimiento <= anioFallecimiento;
    }
}

