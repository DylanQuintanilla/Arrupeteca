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
@Table(name = "prestamo", indexes = {
    @Index(name = "idx_prestamo_busqueda", columnList = "borrado_logico, id_usuario, id_estado_prestamo"),
    @Index(name = "idx_prestamo_fechas", columnList = "fecha_prestamo, fecha_limite_devolucion")
})
@SQLDelete(sql = "UPDATE prestamo SET borrado_logico = 1 WHERE id_prestamo = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Prestamo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud")
    private SolicitudPrestamo solicitudPrestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_prestamo", nullable = false)
    private EstadoPrestamo estadoPrestamo;

    @Column(name = "fecha_prestamo", nullable = false)
    @ToString.Include
    private LocalDateTime fechaPrestamo;

    @Column(name = "fecha_limite_devolucion", nullable = false)
    @ToString.Include
    private LocalDateTime fechaLimiteDevolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entregado_por", nullable = false)
    private Usuario entregadoPor;

    @Column(name = "comentarios", length = 255)
    private String comentarios;

    @Column(name = "borrado_logico", nullable = false)
    @Builder.Default
    private boolean borradoLogico = false;
}
