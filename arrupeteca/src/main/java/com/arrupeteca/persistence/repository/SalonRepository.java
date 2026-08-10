package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Salon;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalonRepository extends JpaRepository<Salon, Long> {

    @EntityGraph(attributePaths = {"planta", "planta.edificio"})
    @Query("""
        SELECT s FROM Salon s
        WHERE
            (:borradoLogico IS NULL OR s.borradoLogico = :borradoLogico)
            AND (:idPlanta IS NULL OR s.planta.id = :idPlanta)
            AND (:nombre IS NULL OR :nombre = '' OR LOWER(s.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
    """)
    Page<Salon> busquedaAdmin(
            @Param("idPlanta") Long idPlanta,
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Salon s SET s.borradoLogico = true WHERE s.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Salon s SET s.borradoLogico = false WHERE s.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCaseAndPlantaId(String nombre, Long idPlanta);

    @EntityGraph(attributePaths = {"planta", "planta.edificio"})
    @Query("SELECT s FROM Salon s WHERE s.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(s.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY s.nombre ASC")
    List<Salon> buscarActivosPorNombre(@Param("nombre") String nombre);
}
