package com.arrupeteca.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "editorial")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SQLDelete(sql = "UPDATE editorial SET borrado_logico = 1 WHERE id_editorial = ?")
public class Editorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_editorial")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    @ToString.Include
    private String nombre;

    @Column(length = 150)
    @ToString.Include
    private String direccion;

    @Column(length = 100, nullable = false, unique = true)
    @ToString.Include
    private String correo;

    @Column(length = 9, nullable = false)
    @ToString.Include
    private String telefono;

    @Column(name = "persona_contacto", nullable = false, length = 100)
    @ToString.Include
    private String personaContacto;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    @ToString.Include
    private boolean borradoLogico = false;
}
