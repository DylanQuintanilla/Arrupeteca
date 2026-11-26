package com.arrupeteca.prestamo.persistence.repository;

import com.arrupeteca.prestamo.dto.response.summary.SolicitudRenovacionResumen;
import com.arrupeteca.prestamo.persistence.entity.SolicitudRenovacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SolicitudRenovacionRepository extends JpaRepository<SolicitudRenovacion, Long> {

    @Query("""
        SELECT new com.arrupeteca.prestamo.dto.response.summary.SolicitudRenovacionResumen(
            r.id,
            e.id,
            u.id,
            CONCAT(u.nombres, ' ', u.apellidos),
            st.id,
            st.nombre,
            r.diasSolicitados,
            r.fechaSolicitud,
            r.borradoLogico
        )
        FROM SolicitudRenovacion r
        JOIN r.ejemplarPrestamo e
        JOIN r.usuario u
        JOIN r.estadoSolicitud st
        WHERE
            (:borradoLogico IS NULL OR r.borradoLogico = :borradoLogico)
            AND (:idUsuario IS NULL OR u.id = :idUsuario)
            AND (:idEstadoSolicitud IS NULL OR st.id = :idEstadoSolicitud)
            AND (:fechaInicio IS NULL OR r.fechaSolicitud >= :fechaInicio)
            AND (:fechaFin IS NULL OR r.fechaSolicitud <= :fechaFin)
    """)
    Page<SolicitudRenovacionResumen> busqueda(
            @Param("borradoLogico") Boolean borradoLogico,
            @Param("idUsuario") Long idUsuario,
            @Param("idEstadoSolicitud") Long idEstadoSolicitud,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);

    @EntityGraph(attributePaths = {"ejemplarPrestamo", "ejemplarPrestamo.prestamo", "usuario", "estadoSolicitud", "revisadoPor"})
    @Query("SELECT r FROM SolicitudRenovacion r WHERE r.id = :id AND (:borradoLogico IS NULL OR r.borradoLogico = :borradoLogico)")
    Optional<SolicitudRenovacion> busquedaPorId(@Param("id") Long id, @Param("borradoLogico") Boolean borradoLogico);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SolicitudRenovacion r SET r.borradoLogico = true WHERE r.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SolicitudRenovacion r SET r.borradoLogico = false WHERE r.id = :id")
    void activar(@Param("id") Long id);
}
