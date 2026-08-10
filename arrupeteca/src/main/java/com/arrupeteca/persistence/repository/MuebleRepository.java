package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Mueble;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MuebleRepository extends JpaRepository<Mueble, Long> {

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    @Query("""
        SELECT m FROM Mueble m
        WHERE
            (:borradoLogico IS NULL OR m.borradoLogico = :borradoLogico)
            AND (:idSalon IS NULL OR m.salon.id = :idSalon)
            AND (:nombre IS NULL OR :nombre = '' OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
    """)
    Page<Mueble> busquedaAdmin(
            @Param("idSalon") Long idSalon,
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Mueble m SET m.borradoLogico = true WHERE m.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Mueble m SET m.borradoLogico = false WHERE m.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCaseAndSalonId(String nombre, Long idSalon);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    @Query("SELECT m FROM Mueble m WHERE m.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY m.nombre ASC")
    List<Mueble> buscarActivosPorNombre(@Param("nombre") String nombre);
}
