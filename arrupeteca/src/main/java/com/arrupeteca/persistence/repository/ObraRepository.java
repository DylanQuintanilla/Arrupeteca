package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.projection.ObraResumen;
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
public interface ObraRepository extends JpaRepository<Obra, Long> {

    //USUARIOS (Lectores / Estudiantes)

    @EntityGraph(attributePaths = {"generos", "categorias", "obraAutores", "obraAutores.autor", "obraAutores.tipoAutoria"})
    List<ObraResumen> findByBorradoLogicoFalse();

    @EntityGraph(attributePaths = {"generos", "categorias", "obraAutores", "obraAutores.autor", "obraAutores.tipoAutoria"})
    Optional<ObraResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @Query("""
        SELECT DISTINCT o FROM Obra o
        LEFT JOIN o.generos g
        LEFT JOIN o.categorias c
        LEFT JOIN o.obraAutores oa
        LEFT JOIN oa.autor a
        WHERE o.borradoLogico = false
          AND (
              :palabra IS NULL OR :palabra = '' OR
              LOWER(o.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
              LOWER(a.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
              LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
              LOWER(g.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
              LOWER(c.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
              )
    """)
    List<ObraResumen> busquedaGlobalAvanzada(@Param("palabra") String palabra, Sort sort);

    // ADMIN (Bibliotecarios / Personal)

    @EntityGraph(attributePaths = {"generos", "categorias", "obraAutores", "obraAutores.autor", "obraAutores.tipoAutoria"})
    List<ObraResumen> findAllProjectedBy();

    @EntityGraph(attributePaths = {"generos", "categorias", "obraAutores", "obraAutores.autor", "obraAutores.tipoAutoria"})
    Optional<ObraResumen> findProjectedById(Long id);

    @Query("""
        SELECT DISTINCT o FROM Obra o
        LEFT JOIN o.generos g
        LEFT JOIN o.categorias c
        LEFT JOIN o.obraAutores oa
        LEFT JOIN oa.autor a
        WHERE
            ( :esBorrado IS NULL OR o.borradoLogico = :esBorrado )
            AND ( :idGenero IS NULL OR g.id = :idGenero )
            AND ( :idCategoria IS NULL OR c.id = :idCategoria )
            AND ( :idAutor IS NULL OR a.id = :idAutor )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(o.resumen) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
            AND ( :anioInicio IS NULL OR o.anioCreacion >= :anioInicio )
            AND ( :anioFin IS NULL OR o.anioCreacion <= :anioFin )
    """)
    List<ObraResumen> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
            @Param("idAutor") Long idAutor,
            @Param("termino") String termino,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Sort sort
    );

    @Modifying
    @Query("UPDATE Obra o SET o.borradoLogico = true WHERE o.id = :id")
    void desactivarObra(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Obra o SET o.borradoLogico = false WHERE o.id = :id")
    void activarObra(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByTituloIgnoreCase(String titulo);
}
