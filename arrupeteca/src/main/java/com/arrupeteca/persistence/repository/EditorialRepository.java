package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Editorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EditorialRepository extends JpaRepository<Editorial, Long> {

    @Query("""
        SELECT e FROM Editorial e
        WHERE
            (:borradoLogico IS NULL OR e.borradoLogico = :borradoLogico)
            AND (:nombre IS NULL OR :nombre = '' OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
            AND (:correo IS NULL OR :correo = '' OR LOWER(e.correo) LIKE LOWER(CONCAT('%', :correo, '%')))
            AND (:personaContacto IS NULL OR :personaContacto = '' OR LOWER(e.personaContacto) LIKE LOWER(CONCAT('%', :personaContacto, '%')))
    """)
    Page<Editorial> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("correo") String correo,
            @Param("personaContacto") String personaContacto,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Editorial e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Editorial e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByCorreoIgnoreCase(String correo);

    @Query("SELECT e FROM Editorial e WHERE e.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY e.nombre ASC")
    List<Editorial> buscarActivosPorNombre(@Param("nombre") String nombre);
}
