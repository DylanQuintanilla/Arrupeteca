package com.arrupeteca.transferencia.internal.entity;

import com.arrupeteca.persistence.entity.Auditable;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.entity.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "solicitud_transferencia", indexes = {
    @Index(name = "idx_solicitud_transferencia_busqueda", columnList = "borrado_logico, id_usuario, id_salon_destino, id_estado_solicitud_transferencia")
})
@SQLDelete(sql = "UPDATE solicitud_transferencia SET borrado_logico = 1 WHERE id_solicitud_transferencia = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SolicitudTransferencia extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_transferencia", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_salon_destino", nullable = false)
    private Salon salonDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_solicitud_transferencia", nullable = false)
    private EstadoSolicitudTransferencia estadoSolicitudTransferencia;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "motivo_rechazo", length = 255)
    private String motivoRechazo;

    @Column(name = "borrado_logico", nullable = false)
    @Builder.Default
    private boolean borradoLogico = false;

    @Builder.Default
    @OneToMany(mappedBy = "solicitudTransferencia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DetalleSolicitudTransferencia> detalles = new HashSet<>();
}
