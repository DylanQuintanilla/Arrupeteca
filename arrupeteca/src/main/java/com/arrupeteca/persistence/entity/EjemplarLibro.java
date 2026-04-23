package com.arrupeteca.persistence.entity;

import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "ejemplar_libro")//AGREGAR EL FK DE DONACION MAS ADELANTE
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EjemplarLibro extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejemplar_libro")
    @EqualsAndHashCode.Include
    private Long id;

    @Size(max = 255, message = "El comentario no puede exceder los 255 caracteres")
    @Column(nullable = true)
    private String comentario;

    @Column(nullable = false, name = "borrado_logico", columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @NotNull(message = "Se debe elegir un libro")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", nullable = false)
    @ToString.Exclude
    private Libro libro;

    @NotNull(message = "Por favor ingresar donde se econtrara el ejemplar")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mueble", nullable = false)
    @ToString.Exclude
    private Mueble mueble;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false)
    private EstadoFisico estadoFisico;

    @Enumerated(EnumType.STRING)
    @Column(name = "disponibilidad", nullable = false)
    private Disponibilidad disponibilidad;

}
