package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
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

    @Query("SELECT new com.arrupeteca.dto.response.summary.EjemplarLibroResumen(" +
           "e.id, e.comentario, e.borradoLogico, ef.nombre, d.nombre, o.titulo, m.nombre) " +
           "FROM EjemplarLibro e " +
           "LEFT JOIN e.libro l " +
           "LEFT JOIN l.obra o " +
           "LEFT JOIN e.estadoFisico ef " +
           "LEFT JOIN e.disponibilidad d " +
           "LEFT JOIN e.mueble m " +
           "WHERE (:esBorrado IS NULL OR e.borradoLogico = :esBorrado) " +
           "AND (:idLibro IS NULL OR l.id = :idLibro) " +
           "AND (:idEstadoFisico IS NULL OR ef.id = :idEstadoFisico) " +
           "AND (:idDisponibilidad IS NULL OR d.id = :idDisponibilidad) " +
           "AND (:idMueble IS NULL OR m.id = :idMueble) " +
           "AND (:idGenero IS NULL OR EXISTS (SELECT 1 FROM o.generos g WHERE g.id = :idGenero)) " +
           "AND (:idCategoria IS NULL OR EXISTS (SELECT 1 FROM o.categorias c WHERE c.id = :idCategoria)) " +
           "AND (:idEtiqueta IS NULL OR EXISTS (SELECT 1 FROM o.etiquetas et WHERE et.id = :idEtiqueta)) " +
           "AND (:termino IS NULL OR LOWER(e.comentario) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(l.isbn) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%')))")
    Page<EjemplarLibroResumen> busquedaMaestraAdminProyectada(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idLibro") Long idLibro,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
            @Param("idEtiqueta") Long idEtiqueta,
            @Param("idMueble") Long idMueble,
            @Param("idEstadoFisico") Long idEstadoFisico,
            @Param("idDisponibilidad") Long idDisponibilidad,
            @Param("termino") String termino,
            Pageable pageable);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Optional<EjemplarLibro> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Optional<EjemplarLibro> findById(Long id);

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
