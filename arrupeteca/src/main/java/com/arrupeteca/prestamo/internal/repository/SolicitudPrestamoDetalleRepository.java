package com.arrupeteca.prestamo.internal.repository;

import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamoDetalle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SolicitudPrestamoDetalleRepository extends JpaRepository<SolicitudPrestamoDetalle, Long> {

    @EntityGraph(attributePaths = {"solicitudPrestamo", "libro", "libro.obra"})
    List<SolicitudPrestamoDetalle> findBySolicitudPrestamoIdAndBorradoLogicoFalse(Long idSolicitud);

    @EntityGraph(attributePaths = {"solicitudPrestamo", "libro", "libro.obra"})
    @Query("SELECT d FROM SolicitudPrestamoDetalle d WHERE d.id = :id AND (:borradoLogico IS NULL OR d.borradoLogico = :borradoLogico)")
    Optional<SolicitudPrestamoDetalle> busquedaPorId(@Param("id") Long id, @Param("borradoLogico") Boolean borradoLogico);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SolicitudPrestamoDetalle d SET d.borradoLogico = true WHERE d.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SolicitudPrestamoDetalle d SET d.borradoLogico = false WHERE d.id = :id")
    void activar(@Param("id") Long id);
}
