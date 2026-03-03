package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.projection.AutorResumen;
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

    // --- USUARIOS (Alumnos / Maestros) ---

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Page<AutorResumen> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<AutorResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @Query(value = """
        SELECT a FROM Autor a
        LEFT JOIN FETCH a.paisNacimiento p
        LEFT JOIN FETCH a.nacionalidad n
        LEFT JOIN FETCH a.generoPrincipal g
        LEFT JOIN FETCH a.obraCumbre o
        WHERE
            a.borradoLogico = false
            AND ( :inicio IS NULL OR a.anioNacimiento >= :inicio )
            AND ( :fin IS NULL OR a.anioNacimiento <= :fin )
            AND (
                :palabra IS NULL OR :palabra = '' OR
                (
                    LOWER(g.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(o.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(a.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(p.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(n.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
                )
            )
    """,
            countQuery = """
        SELECT COUNT(a) FROM Autor a
        LEFT JOIN a.paisNacimiento p
        LEFT JOIN a.nacionalidad n
        LEFT JOIN a.generoPrincipal g
        LEFT JOIN a.obraCumbre o
        WHERE
            a.borradoLogico = false
            AND ( :inicio IS NULL OR a.anioNacimiento >= :inicio )
            AND ( :fin IS NULL OR a.anioNacimiento <= :fin )
            AND (
                :palabra IS NULL OR :palabra = '' OR
                (
                    LOWER(g.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(o.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(a.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(p.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(n.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
                )
            )
    """)
    Page<AutorResumen> busquedaGlobalAvanzada(
            @Param("palabra") String palabra,
            @Param("inicio") Integer inicio,
            @Param("fin") Integer fin,
            Pageable pageable
    );

    // --- ADMIN ---

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Page<AutorResumen> findAllProjectedBy(Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<AutorResumen> findProjectedById(Long id);

    @Query(value = """
        SELECT a FROM Autor a
        LEFT JOIN FETCH a.nacionalidad n
        LEFT JOIN FETCH a.paisNacimiento p
        LEFT JOIN FETCH a.generoPrincipal g
        LEFT JOIN FETCH a.obraCumbre o
        WHERE
            ( :esBorrado IS NULL OR a.borradoLogico = :esBorrado )
            AND ( :idNacionalidad IS NULL OR n.id = :idNacionalidad )
            AND ( :idPais IS NULL OR p.id = :idPais )
            AND ( :idGenero IS NULL OR g.id = :idGenero )
            AND ( :idObra IS NULL OR o.id = :idObra )
            AND (
                :terminoBusqueda IS NULL OR :terminoBusqueda = '' OR
                (
                    LOWER(a.nombre) LIKE LOWER(CONCAT('%', :terminoBusqueda, '%'))
                    OR
                    LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :terminoBusqueda, '%'))
                )
            )
            AND ( :anioInicio IS NULL OR a.anioNacimiento >= :anioInicio )
            AND ( :anioFin IS NULL OR a.anioNacimiento <= :anioFin )
    """,
            countQuery = """
        SELECT COUNT(a) FROM Autor a
        LEFT JOIN a.nacionalidad n
        LEFT JOIN a.paisNacimiento p
        LEFT JOIN a.generoPrincipal g
        LEFT JOIN a.obraCumbre o
        WHERE
            ( :esBorrado IS NULL OR a.borradoLogico = :esBorrado )
            AND ( :idNacionalidad IS NULL OR n.id = :idNacionalidad )
            AND ( :idPais IS NULL OR p.id = :idPais )
            AND ( :idGenero IS NULL OR g.id = :idGenero )
            AND ( :idObra IS NULL OR o.id = :idObra )
            AND (
                :terminoBusqueda IS NULL OR :terminoBusqueda = '' OR
                (
                    LOWER(a.nombre) LIKE LOWER(CONCAT('%', :terminoBusqueda, '%'))
                    OR
                    LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :terminoBusqueda, '%'))
                )
            )
            AND ( :anioInicio IS NULL OR a.anioNacimiento >= :anioInicio )
            AND ( :anioFin IS NULL OR a.anioNacimiento <= :anioFin )
    """)
    Page<AutorResumen> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idNacionalidad") Long idNacionalidad,
            @Param("idPais") Long idPais,
            @Param("idGenero") Long idGenero,
            @Param("idObra") Long idObra,
            @Param("terminoBusqueda") String terminoBusqueda,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Autor a SET a.borradoLogico = true WHERE a.id = :id")
    void desactivarAutor(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Autor a SET a.borradoLogico = false WHERE a.id = :id")
    void activarAutor(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsBySeudonimoIgnoreCase(String seudonimo);

    @Query(value = """
        SELECT a FROM Autor a
        WHERE 
            a.borradoLogico = false
            AND a.anioNacimiento <= :fin 
            AND (a.anioFallecimiento >= :inicio OR a.anioFallecimiento IS NULL)
    """,
            countQuery = """
        SELECT COUNT(a) FROM Autor a
        WHERE 
            a.borradoLogico = false
            AND a.anioNacimiento <= :fin 
            AND (a.anioFallecimiento >= :inicio OR a.anioFallecimiento IS NULL)
    """)
    Page<AutorResumen> buscarAutoresVivosEnRango(@Param("inicio") Integer anioInicio,
                                                 @Param("fin") Integer anioFin,
                                                 Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    @Query("""
    SELECT a FROM Autor a
    WHERE 
        (
           LOWER(a.nombre) = LOWER(:termino) 
           OR 
           LOWER(a.seudonimo) = LOWER(:termino)
        )
        AND (:idExcluir IS NULL OR a.id <> :idExcluir)
        AND a.borradoLogico = false
""")
    List<AutorResumen> buscarPosiblesDuplicados(
            @Param("termino") String termino,
            @Param("idExcluir") Long idExcluir
    );

}