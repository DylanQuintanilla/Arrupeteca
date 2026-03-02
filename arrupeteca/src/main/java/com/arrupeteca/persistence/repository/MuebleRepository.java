package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.projection.MuebleResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MuebleRepository extends JpaRepository<Mueble, Long> {

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    Page<MuebleResumen> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    Optional<MuebleResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @Query(value = """
        SELECT m FROM Mueble m
        LEFT JOIN FETCH m.salon s
        LEFT JOIN FETCH s.planta p
        LEFT JOIN FETCH p.edificio e
        WHERE m.borradoLogico = false
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(m.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(s.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(p.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """,
            countQuery = """
        SELECT COUNT(m) FROM Mueble m
        LEFT JOIN m.salon s
        LEFT JOIN s.planta p
        LEFT JOIN p.edificio e
        WHERE m.borradoLogico = false
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(m.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(s.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(p.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """)
    Page<MuebleResumen> busquedaGlobal(@Param("palabra") String palabra, Pageable pageable);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    Page<MuebleResumen> findAllProjectedBy(Pageable pageable);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    Optional<MuebleResumen> findProjectedById(Long id);

    @Query(value = """
        SELECT m FROM Mueble m
        LEFT JOIN FETCH m.salon s
        LEFT JOIN FETCH s.planta p
        LEFT JOIN FETCH p.edificio e
        WHERE
            ( :esBorrado IS NULL OR m.borradoLogico = :esBorrado )
            AND ( :idSalon IS NULL OR s.id = :idSalon )
            AND ( :idPlanta IS NULL OR p.id = :idPlanta )
            AND ( :idEdificio IS NULL OR e.id = :idEdificio )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(m.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(s.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """,
            countQuery = """
        SELECT COUNT(m) FROM Mueble m
        LEFT JOIN m.salon s
        LEFT JOIN s.planta p
        LEFT JOIN p.edificio e
        WHERE
            ( :esBorrado IS NULL OR m.borradoLogico = :esBorrado )
            AND ( :idSalon IS NULL OR s.id = :idSalon )
            AND ( :idPlanta IS NULL OR p.id = :idPlanta )
            AND ( :idEdificio IS NULL OR e.id = :idEdificio )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(m.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(s.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """)
    Page<MuebleResumen> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idSalon") Long idSalon,
            @Param("idPlanta") Long idPlanta,
            @Param("idEdificio") Long idEdificio,
            @Param("termino") String termino,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Mueble m SET m.borradoLogico = true WHERE m.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Mueble m SET m.borradoLogico = false WHERE m.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);
    boolean existsByNombreAndSalonId(String nombre, Long idSalon);
}
