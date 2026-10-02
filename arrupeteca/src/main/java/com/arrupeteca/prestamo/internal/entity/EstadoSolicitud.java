package com.arrupeteca.prestamo.internal.entity;

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
@Entity
@Builder
@Table(name = "estado_solicitud")
@SQLDelete(sql = "UPDATE estado_solicitud SET borrado_logico = 1 WHERE id_estado_solicitud = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EstadoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_solicitud", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    @ToString.Include
    private String nombre;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;
}

