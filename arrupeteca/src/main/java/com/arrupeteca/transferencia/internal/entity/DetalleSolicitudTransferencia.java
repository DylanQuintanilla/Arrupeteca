package com.arrupeteca.transferencia.internal.entity;

import com.arrupeteca.persistence.entity.Libro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "detalle_solicitud_transferencia", uniqueConstraints = {
    @UniqueConstraint(name = "uk_solicitud_libro", columnNames = {"id_solicitud_transferencia", "id_libro"})
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DetalleSolicitudTransferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_solicitud_transferencia", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud_transferencia", nullable = false)
    private SolicitudTransferencia solicitudTransferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;

    @Column(name = "cantidad", nullable = false)
    @ToString.Include
    private Integer cantidad;
}
