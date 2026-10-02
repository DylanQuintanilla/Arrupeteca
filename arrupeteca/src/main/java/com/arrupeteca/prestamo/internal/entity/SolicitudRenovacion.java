package com.arrupeteca.prestamo.internal.entity;

import com.arrupeteca.persistence.entity.Auditable;
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

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "solicitud_renovacion", indexes = {
    @Index(name = "idx_renovacion_busqueda", columnList = "borrado_logico, id_usuario, id_estado_solicitud")
})
@SQLDelete(sql = "UPDATE solicitud_renovacion SET borrado_logico = 1 WHERE id_solicitud_renovacion = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SolicitudRenovacion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_renovacion", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejemplar_prestamo", nullable = false)
    private EjemplarPrestamo ejemplarPrestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_solicitud", nullable = false)
    private EstadoSolicitud estadoSolicitud;

    @Column(name = "dias_solicitados", nullable = false)
    @Builder.Default
    private Integer diasSolicitados = 7;

    @Column(name = "nueva_fecha_limite_propuesta")
    private LocalDateTime nuevaFechaLimitePropuesta;

    @Column(name = "fecha_solicitud", nullable = false)
    @ToString.Include
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisado_por")
    private Usuario revisadoPor;

    @Column(name = "motivo_solicitud", length = 255)
    private String motivoSolicitud;

    @Column(name = "motivo_respuesta", length = 255)
    private String motivoRespuesta;

    @Column(name = "borrado_logico", nullable = false)
    @Builder.Default
    private boolean borradoLogico = false;
}
