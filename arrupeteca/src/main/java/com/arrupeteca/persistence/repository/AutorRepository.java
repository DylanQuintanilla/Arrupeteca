package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Autor;
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
public interface AutorRepository extends JpaRepository<Autor, Long> {

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad"})
    Page<Autor> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad"})
    @Query(value = """
        SELECT a FROM Autor a
        LEFT JOIN a.paisNacimiento p
        LEFT JOIN a.nacionalidad n
        WHERE a.borradoLogico = false
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(a.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(p.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(n.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
        AND (:anioInicio IS NULL OR a.anioNacimiento >= :anioInicio)
        AND (:anioFin IS NULL OR a.anioNacimiento <= :anioFin)
    """,
            countQuery = """
        SELECT COUNT(a) FROM Autor a
        LEFT JOIN a.paisNacimiento p
        LEFT JOIN a.nacionalidad n
        WHERE a.borradoLogico = false
        AND (
            :palabra IS NULL OR :palabra = '' OR
            (
                LOWER(a.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(p.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                LOWER(n.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
            )
        )
        AND (:anioInicio IS NULL OR a.anioNacimiento >= :anioInicio)
        AND (:anioFin IS NULL OR a.anioNacimiento <= :anioFin)
    """)
    Page<Autor> busquedaGlobalAvanzada(
            @Param("palabra") String palabra,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad"})
    Page<Autor> findAllProjectedBy(Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findProjectedById(Long id);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    @Query(value = """
        SELECT DISTINCT a FROM Autor a
        LEFT JOIN a.paisNacimiento p
        LEFT JOIN a.nacionalidad n
        LEFT JOIN a.generoPrincipal g
        LEFT JOIN a.obraCumbre oc
        WHERE
            ( :esBorrado IS NULL OR a.borradoLogico = :esBorrado )
            AND ( :idNacionalidad IS NULL OR n.id = :idNacionalidad )
            AND ( :idPais IS NULL OR p.id = :idPais )
            AND ( :idGenero IS NULL OR g.id = :idGenero )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """,
            countQuery = """
        SELECT COUNT(DISTINCT a) FROM Autor a
        LEFT JOIN a.paisNacimiento p
        LEFT JOIN a.nacionalidad n
        LEFT JOIN a.generoPrincipal g
        LEFT JOIN a.obraCumbre oc
        WHERE
            ( :esBorrado IS NULL OR a.borradoLogico = :esBorrado )
            AND ( :idNacionalidad IS NULL OR n.id = :idNacionalidad )
            AND ( :idPais IS NULL OR p.id = :idPais )
            AND ( :idGenero IS NULL OR g.id = :idGenero )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """)
    Page<Autor> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idNacionalidad") Long idNacionalidad,
            @Param("idPais") Long idPais,
            @Param("idGenero") Long idGenero,
            @Param("termino") String termino,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad"})
    @Query("""
        SELECT a FROM Autor a
        WHERE (
            LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
            OR (a.seudonimo IS NOT NULL AND LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%')))
        )
        AND (:idExcluir IS NULL OR a.id != :idExcluir)
    """)
    List<Autor> buscarPosiblesDuplicados(
            @Param("termino") String termino,
            @Param("idExcluir") Long idExcluir
    );

    @Modifying
    @Query("UPDATE Autor a SET a.borradoLogico = true WHERE a.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Autor a SET a.borradoLogico = false WHERE a.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
