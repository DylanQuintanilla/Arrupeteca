package com.arrupeteca.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @Column(name = "creado_por", updatable = false)
    @CreatedBy
    private String creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime fechaCreacion;

    @Column(name = "actualizado_por")
    @LastModifiedBy
    private String actualizadoPor;

    @Column(name = "fecha_actualizacion")
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}