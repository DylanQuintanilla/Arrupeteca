package com.arrupeteca.prestamo.internal.repository;

import com.arrupeteca.prestamo.dto.response.summary.SolicitudPrestamoResumen;
import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SolicitudPrestamoRepository extends JpaRepository<SolicitudPrestamo, Long> {

    @Query("""
        SELECT new com.arrupeteca.prestamo.dto.response.summary.SolicitudPrestamoResumen(
            s.id,
            u.id,
            CONCAT(u.nombres, ' ', u.apellidos),
            e.id,
            e.nombre,
            s.fechaSolicitud,
            s.fechaLimiteRetiro,
            s.creadoPorAdmin,
            s.borradoLogico
        )
        FROM SolicitudPrestamo s
        JOIN s.usuario u
        JOIN s.estadoSolicitud e
        WHERE
            (:borradoLogico IS NULL OR s.borradoLogico = :borradoLogico)
            AND (:idUsuario IS NULL OR u.id = :idUsuario)
            AND (:idEstadoSolicitud IS NULL OR e.id = :idEstadoSolicitud)
            AND (:fechaInicio IS NULL OR s.fechaSolicitud >= :fechaInicio)
            AND (:fechaFin IS NULL OR s.fechaSolicitud <= :fechaFin)
    """)
    Page<SolicitudPrestamoResumen> busqueda(
            @Param("borradoLogico") Boolean borradoLogico,
            @Param("idUsuario") Long idUsuario,
            @Param("idEstadoSolicitud") Long idEstadoSolicitud,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);

    @EntityGraph(attributePaths = {"usuario", "estadoSolicitud", "revisadoPor"})
    @Query("SELECT s FROM SolicitudPrestamo s WHERE s.id = :id AND (:borradoLogico IS NULL OR s.borradoLogico = :borradoLogico)")
    Optional<SolicitudPrestamo> busquedaPorId(@Param("id") Long id, @Param("borradoLogico") Boolean borradoLogico);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SolicitudPrestamo s SET s.borradoLogico = true WHERE s.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SolicitudPrestamo s SET s.borradoLogico = false WHERE s.id = :id")
    void activar(@Param("id") Long id);
}
