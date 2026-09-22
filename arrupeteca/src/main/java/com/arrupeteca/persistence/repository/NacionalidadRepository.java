package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Nacionalidad;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NacionalidadRepository extends JpaRepository<Nacionalidad, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Nacionalidad n SET n.borradoLogico = true WHERE n.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Nacionalidad n SET n.borradoLogico = false WHERE n.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    @Query("SELECT e FROM Nacionalidad e WHERE " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:borradoLogico IS NULL OR e.borradoLogico = :borradoLogico)")
    Page<Nacionalidad> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Query("SELECT n FROM Nacionalidad n WHERE n.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(n.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY n.nombre ASC")
    List<Nacionalidad> buscarActivosPorNombre(@Param("nombre") String nombre);
}
