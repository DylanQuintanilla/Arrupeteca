package com.arrupeteca.transferencia.internal.entity;

import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.EstadoFisico;
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
@Table(name = "ejemplar_transferencia", uniqueConstraints = {
    @UniqueConstraint(name = "uk_transferencia_ejemplar", columnNames = {"id_transferencia", "id_ejemplar_libro"})
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EjemplarTransferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejemplar_transferencia", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transferencia", nullable = false)
    private Transferencia transferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejemplar_libro", nullable = false)
    private EjemplarLibro ejemplarLibro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_fisico_al_momento", nullable = false)
    private EstadoFisico estadoFisicoAlMomento;

    @Column(name = "recibido", nullable = false)
    @Builder.Default
    private boolean recibido = false;
}
