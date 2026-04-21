package com.arrupeteca.persistence.entity;

import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
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

    @Size(message = "El comentario no puede exceder los 255 caracteres")
    @Column(nullable = true)
    private String comentario;

    @Column(nullable = false, name = "borrado_logico", columnDefinition = "TINYINT(1) DEFAULT 0")
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

    //private Donacion donacion;

}
