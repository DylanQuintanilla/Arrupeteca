package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
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
public interface EjemplarLibroRepository extends JpaRepository<EjemplarLibro, Long> {

    //USUARIOS (Alumnos / Lectores)

    @EntityGraph(attributePaths = {
            "libro", "libro.obra", "libro.editorial",
            "mueble", "mueble.salon", "mueble.salon.planta", "mueble.salon.planta.edificio"
    })
    List<EjemplarLibroResumen> findByBorradoLogicoFalse();

    @EntityGraph(attributePaths = {
            "libro", "libro.obra", "libro.editorial",
            "mueble", "mueble.salon", "mueble.salon.planta", "mueble.salon.planta.edificio"
    })
    Optional<EjemplarLibroResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @Query("""
        SELECT el FROM EjemplarLibro el
        LEFT JOIN FETCH el.libro l
        LEFT JOIN FETCH l.obra o
        LEFT JOIN FETCH el.mueble m
        LEFT JOIN FETCH m.salon s
        LEFT JOIN FETCH s.planta p
        LEFT JOIN FETCH p.edificio e
        WHERE el.borradoLogico = false
          AND (
              :palabra IS NULL OR :palabra = '' OR
              LOWER(o.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
              LOWER(l.isbn) LIKE LOWER(CONCAT('%', :palabra, '%')) OR
              LOWER(m.nombre) LIKE LOWER(CONCAT('%', :palabra, '%'))
          )
    """)
    List<EjemplarLibroResumen> busquedaGlobalAvanzada(@Param("palabra") String palabra, Sort sort);

    //ADMIN (Bibliotecarios / Personal)

    @EntityGraph(attributePaths = {
            "libro", "libro.obra", "libro.editorial",
            "mueble", "mueble.salon", "mueble.salon.planta", "mueble.salon.planta.edificio"
    })
    List<EjemplarLibroResumen> findAllProjectedBy();

    @EntityGraph(attributePaths = {
            "libro", "libro.obra", "libro.editorial",
            "mueble", "mueble.salon", "mueble.salon.planta", "mueble.salon.planta.edificio"
    })
    Optional<EjemplarLibroResumen> findProjectedById(Long id);

    @Query("""
        SELECT el FROM EjemplarLibro el
        LEFT JOIN FETCH el.libro l
        LEFT JOIN FETCH l.obra o
        LEFT JOIN FETCH el.mueble m
        LEFT JOIN FETCH m.salon s
        LEFT JOIN FETCH s.planta p
        LEFT JOIN FETCH p.edificio e
        WHERE
            ( :esBorrado IS NULL OR el.borradoLogico = :esBorrado )
            AND ( :idLibro IS NULL OR l.id = :idLibro )
            AND ( :idMueble IS NULL OR m.id = :idMueble )
            AND ( :estadoFisico IS NULL OR el.estadoFisico = :estadoFisico )
            AND ( :disponibilidad IS NULL OR el.disponibilidad = :disponibilidad )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(el.comentario) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """)
    List<EjemplarLibroResumen> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idLibro") Long idLibro,
            @Param("idMueble") Long idMueble,
            @Param("estadoFisico") EstadoFisico estadoFisico,
            @Param("disponibilidad") Disponibilidad disponibilidad,
            @Param("termino") String termino,
            Sort sort
    );

    @Modifying
    @Query("UPDATE EjemplarLibro el SET el.borradoLogico = true WHERE el.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE EjemplarLibro el SET el.borradoLogico = false WHERE el.id = :id")
    void activar(@Param("id") Long id);

    long countByLibro_IdAndDisponibilidadAndBorradoLogicoFalse(Long idLibro, Disponibilidad disponibilidad);
}
