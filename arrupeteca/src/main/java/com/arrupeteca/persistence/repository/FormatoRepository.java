package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Formato;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FormatoRepository extends JpaRepository<Formato, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Formato f SET f.borradoLogico = true WHERE f.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Formato f SET f.borradoLogico = false WHERE f.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    @Query("SELECT e FROM Formato e WHERE " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:borradoLogico IS NULL OR e.borradoLogico = :borradoLogico)")
    Page<Formato> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Query("SELECT f FROM Formato f WHERE f.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(f.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY f.nombre ASC")
    List<Formato> buscarActivosPorNombre(@Param("nombre") String nombre);
}
