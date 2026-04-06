package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.projection.LibroResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Page<LibroResumen> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Optional<LibroResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    @Query(value = """
        SELECT l FROM Libro l
        LEFT JOIN l.obra o
        LEFT JOIN l.editorial e
        LEFT JOIN l.formato f
        WHERE l.borradoLogico = false
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(l.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(o.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(f.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """,
            countQuery = """
        SELECT COUNT(l) FROM Libro l
        LEFT JOIN l.obra o
        LEFT JOIN l.editorial e
        LEFT JOIN l.formato f
        WHERE l.borradoLogico = false
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(l.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(o.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(f.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """)
    Page<LibroResumen> busquedaGlobal(@Param("palabra") String palabra, Pageable pageable);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Page<LibroResumen> findAllProjectedBy(Pageable pageable);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Optional<LibroResumen> findProjectedById(Long id);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    @Query(value = """
        SELECT l FROM Libro l
        LEFT JOIN l.obra o
        LEFT JOIN l.editorial e
        LEFT JOIN l.formato f
        WHERE
            ( :esBorrado IS NULL OR l.borradoLogico = :esBorrado )
            AND ( :idObra IS NULL OR o.id = :idObra )
            AND ( :idEditorial IS NULL OR e.id = :idEditorial )
            AND ( :idFormato IS NULL OR f.id = :idFormato )
            AND ( :grado IS NULL OR l.grado = :grado )
            AND ( :ciclo IS NULL OR l.ciclo = :ciclo )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(l.isbn) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
            AND ( :anioInicio IS NULL OR l.anioPublicacion >= :anioInicio )
            AND ( :anioFin IS NULL OR l.anioPublicacion <= :anioFin )
    """,
            countQuery = """
        SELECT COUNT(l) FROM Libro l
        LEFT JOIN l.obra o
        LEFT JOIN l.editorial e
        LEFT JOIN l.formato f
        WHERE
            ( :esBorrado IS NULL OR l.borradoLogico = :esBorrado )
            AND ( :idObra IS NULL OR o.id = :idObra )
            AND ( :idEditorial IS NULL OR e.id = :idEditorial )
            AND ( :idFormato IS NULL OR f.id = :idFormato )
            AND ( :grado IS NULL OR l.grado = :grado )
            AND ( :ciclo IS NULL OR l.ciclo = :ciclo )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(l.isbn) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
            AND ( :anioInicio IS NULL OR l.anioPublicacion >= :anioInicio )
            AND ( :anioFin IS NULL OR l.anioPublicacion <= :anioFin )
    """)
    Page<LibroResumen> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idObra") Long idObra,
            @Param("idEditorial") Long idEditorial,
            @Param("idFormato") Long idFormato,
            @Param("grado") Object grado,
            @Param("ciclo") Object ciclo,
            @Param("termino") String termino,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Page<LibroResumen> findByObraId(Pageable pageable, Long obraId);

    @Modifying
    @Query("UPDATE Libro l SET l.borradoLogico = true WHERE l.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Libro l SET l.borradoLogico = false WHERE l.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByIsbn(String isbn);
}