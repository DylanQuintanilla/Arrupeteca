package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.EjemplarLibro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EjemplarLibroRepository extends JpaRepository<EjemplarLibro, Long> {

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Page<EjemplarLibro> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Optional<EjemplarLibro> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    @Query(value = """
        SELECT DISTINCT e FROM EjemplarLibro e
        LEFT JOIN e.libro.obra.generos g
        LEFT JOIN e.libro.obra.categorias c
        WHERE e.borradoLogico = false
        AND (:idGenero IS NULL OR g.id = :idGenero)
        AND (:idCategoria IS NULL OR c.id = :idCategoria)
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(e.libro.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.libro.obra.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.comentario) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """,
            countQuery = """
        SELECT COUNT(DISTINCT e) FROM EjemplarLibro e
        LEFT JOIN e.libro.obra.generos g
        LEFT JOIN e.libro.obra.categorias c
        WHERE e.borradoLogico = false
        AND (:idGenero IS NULL OR g.id = :idGenero)
        AND (:idCategoria IS NULL OR c.id = :idCategoria)
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(e.libro.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.libro.obra.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(e.comentario) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
    """)
    Page<EjemplarLibro> busquedaGlobal(
            @Param("palabra") String palabra,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Page<EjemplarLibro> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Optional<EjemplarLibro> findById(Long id);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    @Query(value = """
        SELECT DISTINCT e FROM EjemplarLibro e
        LEFT JOIN e.libro.obra.generos g
        LEFT JOIN e.libro.obra.categorias c
        WHERE
            ( :esBorrado IS NULL OR e.borradoLogico = :esBorrado )
            AND ( :idLibro IS NULL OR e.libro.id = :idLibro )
            AND ( :idGenero IS NULL OR g.id = :idGenero )
            AND ( :idCategoria IS NULL OR c.id = :idCategoria )
            AND ( :idMueble IS NULL OR e.mueble.id = :idMueble )
            AND ( :estadoFisico IS NULL OR e.estadoFisico = :estadoFisico )
            AND ( :disponibilidad IS NULL OR e.disponibilidad = :disponibilidad )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(e.comentario) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(e.libro.obra.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """,
            countQuery = """
        SELECT COUNT(DISTINCT e) FROM EjemplarLibro e
        LEFT JOIN e.libro.obra.generos g
        LEFT JOIN e.libro.obra.categorias c
        WHERE
            ( :esBorrado IS NULL OR e.borradoLogico = :esBorrado )
            AND ( :idLibro IS NULL OR e.libro.id = :idLibro )
            AND ( :idGenero IS NULL OR g.id = :idGenero )
            AND ( :idCategoria IS NULL OR c.id = :idCategoria )
            AND ( :idMueble IS NULL OR e.mueble.id = :idMueble )
            AND ( :estadoFisico IS NULL OR e.estadoFisico = :estadoFisico )
            AND ( :disponibilidad IS NULL OR e.disponibilidad = :disponibilidad )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(e.comentario) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(e.libro.obra.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """)
    Page<EjemplarLibro> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idLibro") Long idLibro,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
            @Param("idMueble") Long idMueble,
            @Param("estadoFisico") Object estadoFisico,
            @Param("disponibilidad") Object disponibilidad,
            @Param("termino") String termino,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EjemplarLibro e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EjemplarLibro e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM ejemplar_libro WHERE id_ejemplar_libro = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);
}
