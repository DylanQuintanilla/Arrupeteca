package com.arrupeteca.prestamo.internal.entity;

import com.arrupeteca.persistence.entity.Auditable;
import com.arrupeteca.persistence.entity.Libro;
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

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "solicitud_prestamo_detalle", indexes = {
    @Index(name = "idx_solicitud_detalle_solicitud", columnList = "id_solicitud, borrado_logico")
})
@SQLDelete(sql = "UPDATE solicitud_prestamo_detalle SET borrado_logico = 1 WHERE id_solicitud_detalle = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SolicitudPrestamoDetalle extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_detalle", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private SolicitudPrestamo solicitudPrestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;

    @Column(name = "cantidad_solicitada", nullable = false)
    @Builder.Default
    @ToString.Include
    private Integer cantidadSolicitada = 1;

    @Column(name = "borrado_logico", nullable = false)
    @Builder.Default
    private boolean borradoLogico = false;
}
