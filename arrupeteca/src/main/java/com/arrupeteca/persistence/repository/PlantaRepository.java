package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Planta;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlantaRepository extends JpaRepository<Planta, Long> {

    @EntityGraph(attributePaths = {"edificio"})
    @Query("""
        SELECT p FROM Planta p
        WHERE
            (:borradoLogico IS NULL OR p.borradoLogico = :borradoLogico)
            AND (:idEdificio IS NULL OR p.edificio.id = :idEdificio)
            AND (:nombre IS NULL OR :nombre = '' OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
    """)
    Page<Planta> busquedaAdmin(
            @Param("idEdificio") Long idEdificio,
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Planta p SET p.borradoLogico = true WHERE p.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Planta p SET p.borradoLogico = false WHERE p.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCaseAndEdificioId(String nombre, Long idEdificio);

    @EntityGraph(attributePaths = {"edificio"})
    @Query("SELECT p FROM Planta p WHERE p.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY p.nombre ASC")
    List<Planta> buscarActivosPorNombre(@Param("nombre") String nombre);
}
