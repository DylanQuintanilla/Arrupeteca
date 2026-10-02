package com.arrupeteca.prestamo.internal.repository;

import com.arrupeteca.prestamo.dto.response.summary.PrestamoResumen;
import com.arrupeteca.prestamo.internal.entity.Prestamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    @Query("""
        SELECT new com.arrupeteca.prestamo.dto.response.summary.PrestamoResumen(
            p.id,
            s.id,
            u.id,
            CONCAT(u.nombres, ' ', u.apellidos),
            e.id,
            e.nombre,
            p.fechaPrestamo,
            p.fechaLimiteDevolucion,
            p.borradoLogico
        )
        FROM Prestamo p
        LEFT JOIN p.solicitudPrestamo s
        JOIN p.usuario u
        JOIN p.estadoPrestamo e
        WHERE
            (:borradoLogico IS NULL OR p.borradoLogico = :borradoLogico)
            AND (:idUsuario IS NULL OR u.id = :idUsuario)
            AND (:idEstadoPrestamo IS NULL OR e.id = :idEstadoPrestamo)
            AND (:fechaInicio IS NULL OR p.fechaPrestamo >= :fechaInicio)
            AND (:fechaFin IS NULL OR p.fechaPrestamo <= :fechaFin)
    """)
    Page<PrestamoResumen> busqueda(
            @Param("borradoLogico") Boolean borradoLogico,
            @Param("idUsuario") Long idUsuario,
            @Param("idEstadoPrestamo") Long idEstadoPrestamo,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);

    @EntityGraph(attributePaths = {"solicitudPrestamo", "usuario", "estadoPrestamo", "entregadoPor"})
    @Query("SELECT p FROM Prestamo p WHERE p.id = :id AND (:borradoLogico IS NULL OR p.borradoLogico = :borradoLogico)")
    Optional<Prestamo> busquedaPorId(@Param("id") Long id, @Param("borradoLogico") Boolean borradoLogico);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Prestamo p SET p.borradoLogico = true WHERE p.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Prestamo p SET p.borradoLogico = false WHERE p.id = :id")
    void activar(@Param("id") Long id);
}
