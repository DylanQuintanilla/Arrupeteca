package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.arrupeteca.persistence.projection.AutorResumen;
import com.arrupeteca.persistence.projection.LibroResumen;
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
public interface LibroRepository extends JpaRepository<Libro, Long> {

    //USUARIOS (Alumnos / Lectores)

    @EntityGraph(attributePaths = {"obra", "editorial", "formato", "idiomas"})
    List<LibroResumen> findByBorradoLogicoFalse();

    @EntityGraph(attributePaths = {"obra", "editorial" ,"formato", "idiomas"})
    Optional<LibroResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @Query("""
        SELECT DISTINCT l FROM Libro l
        LEFT JOIN FETCH l.editorial e
        LEFT JOIN FETCH l.obra o
        LEFT JOIN FETCH l.formato f
        LEFT JOIN FETCH o.obraAutores oa
        LEFT JOIN FETCH oa.autor a
        WHERE l.borradoLogico = false
            AND (
                :palabra IS NULL OR :palabra = '' OR
                (
                    LOWER(e.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(l.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(o.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(a.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
                    LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :palabra, '%'))
                )
            )
    """)
    List<LibroResumen> busquedaGlobalAvanzada(@Param("palabra") String palabra, Sort sort);

    // ADMIN (Bibliotecarios / Personal)

    @Query("""
        SELECT DISTINCT l FROM Libro l
        LEFT JOIN FETCH l.obra o
        LEFT JOIN FETCH l.editorial e
        LEFT JOIN FETCH l.formato f
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
                    LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(e.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )

            AND ( :anioInicio IS NULL OR l.anioPublicacion >= :anioInicio )
            AND ( :anioFin IS NULL OR l.anioPublicacion <= :anioFin )
    """)
    List<LibroResumen> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idObra") Long idObra,
            @Param("idEditorial") Long idEditorial,
            @Param("idFormato") Long idFormato,
            @Param("grado") Grado grado,
            @Param("ciclo") Ciclo ciclo,
            @Param("termino") String termino,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Sort sort
    );

    @EntityGraph(attributePaths = {"obra", "editorial", "formato", "idiomas"})
    List<LibroResumen> findAllProjectedBy();

    @EntityGraph(attributePaths = {"obra", "editorial", "formato", "idiomas"})
    Optional<LibroResumen> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Libro l SET l.borradoLogico = true WHERE l.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Libro l SET l.borradoLogico = false WHERE l.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByIsbnIgnoreCase(String isbn);


}
