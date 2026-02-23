package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.*;
import com.arrupeteca.persistence.projection.AutorResumen;
import org.springframework.data.domain.Sort;
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

    //USUARIOS (Alumnos / Maestros)

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    List<AutorResumen> findByBorradoLogicoFalse();

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<AutorResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @Query("""
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
    """)
    List<AutorResumen> busquedaGlobalAvanzada(
            @Param("palabra") String palabra,
            @Param("inicio") Integer inicio,
            @Param("fin") Integer fin,
            Sort sort
    );

    // Admin -----------------------

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    List<AutorResumen> findAllProjectedBy();

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<AutorResumen> findProjectedById(Long id);

    @Query("""
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
    """)
    List<AutorResumen> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idNacionalidad") Long idNacionalidad,
            @Param("idPais") Long idPais,
            @Param("idGenero") Long idGenero,
            @Param("idObra") Long idObra,
            @Param("terminoBusqueda") String terminoBusqueda,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Sort sort
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

    @Query("""
        SELECT a FROM Autor a
        WHERE 
            a.borradoLogico = false
            AND a.anioNacimiento <= :fin 
            AND (a.anioFallecimiento >= :inicio OR a.anioFallecimiento IS NULL)
    """)
    List<AutorResumen> buscarAutoresVivosEnRango(@Param("inicio") Integer anioInicio,
                                                 @Param("fin") Integer anioFin);

}
