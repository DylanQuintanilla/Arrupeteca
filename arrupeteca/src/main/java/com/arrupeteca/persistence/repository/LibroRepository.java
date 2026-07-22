package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Page<Libro> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Optional<Libro> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    @Query(value = """
        SELECT DISTINCT l FROM Libro l
        LEFT JOIN l.obra.generos g
        LEFT JOIN l.obra.categorias c
        WHERE l.borradoLogico = false
        AND (:idGenero IS NULL OR g.id = :idGenero)
        AND (:idCategoria IS NULL OR c.id = :idCategoria)
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(l.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(l.obra.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(l.editorial.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(l.formato.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """,
            countQuery = """
        SELECT COUNT(DISTINCT l) FROM Libro l
        LEFT JOIN l.obra.generos g
        LEFT JOIN l.obra.categorias c
        WHERE l.borradoLogico = false
        AND (:idGenero IS NULL OR g.id = :idGenero)
        AND (:idCategoria IS NULL OR c.id = :idCategoria)
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(l.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(l.obra.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(l.editorial.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(l.formato.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """)
    Page<Libro> busquedaGlobal(
            @Param("palabra") String palabra,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Page<Libro> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Optional<Libro> findById(Long id);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    @Query(value = """
        SELECT DISTINCT l FROM Libro l
        LEFT JOIN l.obra.generos g
        LEFT JOIN l.obra.categorias c
        WHERE
            ( :esBorrado IS NULL OR l.borradoLogico = :esBorrado )
            AND ( :idObra IS NULL OR l.obra.id = :idObra )
            AND ( :idGenero IS NULL OR g.id = :idGenero )
            AND ( :idCategoria IS NULL OR c.id = :idCategoria )
            AND ( :idEditorial IS NULL OR l.editorial.id = :idEditorial )
            AND ( :idFormato IS NULL OR l.formato.id = :idFormato )
            AND ( :grado IS NULL OR l.grado = :grado )
            AND ( :ciclo IS NULL OR l.ciclo = :ciclo )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(l.isbn) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(l.obra.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
            AND ( :anioInicio IS NULL OR l.anioPublicacion >= :anioInicio )
            AND ( :anioFin IS NULL OR l.anioPublicacion <= :anioFin )
    """,
            countQuery = """
        SELECT COUNT(DISTINCT l) FROM Libro l
        LEFT JOIN l.obra.generos g
        LEFT JOIN l.obra.categorias c
        WHERE
            ( :esBorrado IS NULL OR l.borradoLogico = :esBorrado )
            AND ( :idObra IS NULL OR l.obra.id = :idObra )
            AND ( :idGenero IS NULL OR g.id = :idGenero )
            AND ( :idCategoria IS NULL OR c.id = :idCategoria )
            AND ( :idEditorial IS NULL OR l.editorial.id = :idEditorial )
            AND ( :idFormato IS NULL OR l.formato.id = :idFormato )
            AND ( :grado IS NULL OR l.grado = :grado )
            AND ( :ciclo IS NULL OR l.ciclo = :ciclo )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(l.isbn) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(l.obra.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
            AND ( :anioInicio IS NULL OR l.anioPublicacion >= :anioInicio )
            AND ( :anioFin IS NULL OR l.anioPublicacion <= :anioFin )
    """)
    Page<Libro> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idObra") Long idObra,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
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
    @Query("SELECT l FROM Libro l WHERE l.obra.id = :obraId")
    Page<Libro> buscarPorObraId(@Param("obraId") Long obraId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Libro l SET l.borradoLogico = true WHERE l.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Libro l SET l.borradoLogico = false WHERE l.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM libro WHERE id_libro = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByIsbn(String isbn);
}
