package com.arrupeteca.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "nacionalidad")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Nacionalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nacionalidad")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(max = 50, message = "El nombreno puede exceder los 50 caracteres")
    @Column(length = 50, unique = true, nullable = false)
    private String nombre;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

}
