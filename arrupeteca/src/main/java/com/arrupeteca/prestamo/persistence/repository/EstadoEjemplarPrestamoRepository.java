package com.arrupeteca.prestamo.persistence.repository;

import com.arrupeteca.prestamo.persistence.entity.EstadoEjemplarPrestamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstadoEjemplarPrestamoRepository extends JpaRepository<EstadoEjemplarPrestamo, Long> {

    @Query("SELECT e FROM EstadoEjemplarPrestamo e WHERE " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:borradoLogico IS NULL OR e.borradoLogico = :borradoLogico)")
    Page<EstadoEjemplarPrestamo> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EstadoEjemplarPrestamo e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EstadoEjemplarPrestamo e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    @Query("SELECT e FROM EstadoEjemplarPrestamo e WHERE e.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY e.nombre ASC")
    List<EstadoEjemplarPrestamo> buscarActivosPorNombre(@Param("nombre") String nombre);
}

