package com.arrupeteca.transferencia.internal.entity;

import com.arrupeteca.persistence.entity.Auditable;
import com.arrupeteca.persistence.entity.Mueble;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "transferencia", indexes = {
    @Index(name = "idx_transferencia_busqueda", columnList = "borrado_logico, id_mueble_origen, id_mueble_destino, id_estado_transferencia")
})
@SQLDelete(sql = "UPDATE transferencia SET borrado_logico = 1 WHERE id_transferencia = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Transferencia extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transferencia", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud_transferencia")
    private SolicitudTransferencia solicitudTransferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_envia", nullable = false)
    private Usuario usuarioEnvia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_recibe")
    private Usuario usuarioRecibe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mueble_origen", nullable = false)
    private Mueble muebleOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mueble_destino", nullable = false)
    private Mueble muebleDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_transferencia", nullable = false)
    private EstadoTransferencia estadoTransferencia;

    @Column(name = "fecha_envio", nullable = false)
    @ToString.Include
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(name = "observacion_recepcion", length = 255)
    private String observacionRecepcion;

    @Column(name = "confirmada", nullable = false)
    @Builder.Default
    private boolean confirmada = false;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "borrado_logico", nullable = false)
    @Builder.Default
    private boolean borradoLogico = false;

    @Builder.Default
    @OneToMany(mappedBy = "transferencia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EjemplarTransferencia> ejemplares = new HashSet<>();
}
