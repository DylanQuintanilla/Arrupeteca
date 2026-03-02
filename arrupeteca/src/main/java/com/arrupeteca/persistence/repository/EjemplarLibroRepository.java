package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
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
public interface EjemplarLibroRepository extends JpaRepository<EjemplarLibro, Long> {

    @EntityGraph(attributePaths = {"libro", "mueble"})
    Page<EjemplarLibroResumen> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"libro", "mueble"})
    Optional<EjemplarLibroResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @Query(value = """
        SELECT e FROM EjemplarLibro e
        LEFT JOIN FETCH e.libro l
        LEFT JOIN FETCH e.mueble m
        WHERE e.borradoLogico = false
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(l.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(l.obra.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.comentario) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(CAST(e.estadoFisico AS string)) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(CAST(e.disponibilidad AS string)) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """,
            countQuery = """
        SELECT COUNT(e) FROM EjemplarLibro e
        LEFT JOIN e.libro l
        WHERE e.borradoLogico = false
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(l.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(l.obra.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.comentario) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(CAST(e.estadoFisico AS string)) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(CAST(e.disponibilidad AS string)) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """)
    Page<EjemplarLibroResumen> busquedaGlobal(@Param("palabra") String palabra, Pageable pageable);

    @EntityGraph(attributePaths = {"libro", "mueble"})
    Page<EjemplarLibroResumen> findAllProjectedBy(Pageable pageable);

    @EntityGraph(attributePaths = {"libro", "mueble"})
    Optional<EjemplarLibroResumen> findProjectedById(Long id);

    @Query(value = """
        SELECT e FROM EjemplarLibro e
        LEFT JOIN FETCH e.libro l
        LEFT JOIN FETCH e.mueble m
        WHERE
            ( :esBorrado IS NULL OR e.borradoLogico = :esBorrado )
            AND ( :idLibro IS NULL OR l.id = :idLibro )
            AND ( :idMueble IS NULL OR m.id = :idMueble )
            AND ( :estadoFisico IS NULL OR e.estadoFisico = :estadoFisico )
            AND ( :disponibilidad IS NULL OR e.disponibilidad = :disponibilidad )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(e.comentario) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(l.obra.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """,
            countQuery = """
        SELECT COUNT(e) FROM EjemplarLibro e
        LEFT JOIN e.libro l
        LEFT JOIN e.mueble m
        WHERE
            ( :esBorrado IS NULL OR e.borradoLogico = :esBorrado )
            AND ( :idLibro IS NULL OR l.id = :idLibro )
            AND ( :idMueble IS NULL OR m.id = :idMueble )
            AND ( :estadoFisico IS NULL OR e.estadoFisico = :estadoFisico )
            AND ( :disponibilidad IS NULL OR e.disponibilidad = :disponibilidad )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(e.comentario) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(l.obra.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """)
    Page<EjemplarLibroResumen> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idLibro") Long idLibro,
            @Param("idMueble") Long idMueble,
            @Param("estadoFisico") Object estadoFisico,
            @Param("disponibilidad") Object disponibilidad,
            @Param("termino") String termino,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE EjemplarLibro e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE EjemplarLibro e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);
}