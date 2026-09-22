package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.LibroResumen;
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

    @Query("SELECT new com.arrupeteca.dto.response.summary.LibroResumen(" +
           "l.id, l.isbn, l.anioPublicacion, l.precio, l.urlImagen, l.mora, " +
           "l.borradoLogico, gr.nombre, ci.nombre, o.titulo, ed.nombre, f.nombre) " +
           "FROM Libro l " +
           "LEFT JOIN l.obra o " +
           "LEFT JOIN l.editorial ed " +
           "LEFT JOIN l.formato f " +
           "LEFT JOIN l.grado gr " +
           "LEFT JOIN l.ciclo ci " +
           "WHERE (:borradoLogico IS NULL OR l.borradoLogico = :borradoLogico) " +
           "AND (:idObra IS NULL OR o.id = :idObra) " +
           "AND (:idAutor IS NULL OR EXISTS (SELECT 1 FROM o.obraAutores oa WHERE oa.autor.id = :idAutor)) " +
           "AND (:idEditorial IS NULL OR ed.id = :idEditorial) " +
           "AND (:idFormato IS NULL OR f.id = :idFormato) " +
           "AND (:idGrado IS NULL OR gr.id = :idGrado) " +
           "AND (:idCiclo IS NULL OR ci.id = :idCiclo) " +
           "AND (:idIdioma IS NULL OR EXISTS (SELECT 1 FROM l.idiomas i WHERE i.id = :idIdioma)) " +
           "AND (:idGenero IS NULL OR EXISTS (SELECT 1 FROM o.generos g WHERE g.id = :idGenero)) " +
           "AND (:idCategoria IS NULL OR EXISTS (SELECT 1 FROM o.categorias c WHERE c.id = :idCategoria)) " +
           "AND (:idEtiqueta IS NULL OR EXISTS (SELECT 1 FROM o.etiquetas e WHERE e.id = :idEtiqueta)) " +
           "AND (:termino IS NULL OR :termino = '' OR LOWER(l.isbn) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:anioInicio IS NULL OR l.anioPublicacion >= :anioInicio) " +
           "AND (:anioFin IS NULL OR l.anioPublicacion <= :anioFin)")
    Page<LibroResumen> busqueda(
            @Param("borradoLogico") Boolean borradoLogico,
            @Param("idObra") Long idObra,
            @Param("idAutor") Long idAutor,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
            @Param("idEtiqueta") Long idEtiqueta,
            @Param("idEditorial") Long idEditorial,
            @Param("idFormato") Long idFormato,
            @Param("idGrado") Long idGrado,
            @Param("idCiclo") Long idCiclo,
            @Param("idIdioma") Long idIdioma,
            @Param("termino") String termino,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Pageable pageable);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato", "grado", "ciclo"})
    @Query("SELECT l FROM Libro l WHERE l.id = :id AND (:borradoLogico IS NULL OR l.borradoLogico = :borradoLogico)")
    Optional<Libro> busquedaPorId(@Param("id") Long id, @Param("borradoLogico") Boolean borradoLogico);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Libro l SET l.borradoLogico = true WHERE l.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Libro l SET l.borradoLogico = false WHERE l.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIsbn(String isbn);
}

