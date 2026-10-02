package com.arrupeteca.prestamo.internal.repository;

import com.arrupeteca.prestamo.internal.entity.EjemplarPrestamo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EjemplarPrestamoRepository extends JpaRepository<EjemplarPrestamo, Long> {

    @EntityGraph(attributePaths = {"prestamo", "ejemplar", "ejemplar.libro", "ejemplar.libro.obra", "estadoEjemplarPrestamo", "estadoFisicoEntrega", "estadoFisicoDevolucion", "recibidoPor"})
    List<EjemplarPrestamo> findByPrestamoIdAndBorradoLogicoFalse(Long idPrestamo);

    @EntityGraph(attributePaths = {"prestamo", "ejemplar", "ejemplar.libro", "ejemplar.libro.obra", "estadoEjemplarPrestamo", "estadoFisicoEntrega", "estadoFisicoDevolucion", "recibidoPor"})
    @Query("SELECT e FROM EjemplarPrestamo e WHERE e.id = :id AND (:borradoLogico IS NULL OR e.borradoLogico = :borradoLogico)")
    Optional<EjemplarPrestamo> busquedaPorId(@Param("id") Long id, @Param("borradoLogico") Boolean borradoLogico);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EjemplarPrestamo e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EjemplarPrestamo e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);
}
