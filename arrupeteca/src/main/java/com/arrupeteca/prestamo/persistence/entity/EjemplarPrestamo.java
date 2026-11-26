package com.arrupeteca.prestamo.persistence.entity;

import com.arrupeteca.persistence.entity.Auditable;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "ejemplar_prestamo", indexes = {
    @Index(name = "idx_ejemplar_prestamo_prestamo", columnList = "id_prestamo, borrado_logico"),
    @Index(name = "idx_ejemplar_prestamo_ejemplar", columnList = "id_ejemplar, id_estado_ejemplar_prestamo")
})
@SQLDelete(sql = "UPDATE ejemplar_prestamo SET borrado_logico = 1 WHERE id_ejemplar_prestamo = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EjemplarPrestamo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejemplar_prestamo", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prestamo", nullable = false)
    private Prestamo prestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejemplar", nullable = false)
    private EjemplarLibro ejemplar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_ejemplar_prestamo", nullable = false)
    private EstadoEjemplarPrestamo estadoEjemplarPrestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_fisico_entrega")
    private EstadoFisico estadoFisicoEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_fisico_devolucion")
    private EstadoFisico estadoFisicoDevolucion;

    @Column(name = "fecha_devolucion_real")
    @ToString.Include
    private LocalDateTime fechaDevolucionReal;

    @Column(name = "mora_calculada", precision = 6, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal moraCalculada = BigDecimal.ZERO;

    @Column(name = "mora_pagada", nullable = false)
    @Builder.Default
    private boolean moraPagada = false;

    @Column(name = "exento_mora", nullable = false)
    @Builder.Default
    private boolean exentoMora = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibido_por")
    private Usuario recibidoPor;

    @Column(name = "borrado_logico", nullable = false)
    @Builder.Default
    private boolean borradoLogico = false;
}
