package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.projection.EditorialResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial,Long> {

    // --- LECTURAS USUARIO ---
    Page<EditorialResumen> findByBorradoLogicoFalse(Pageable pageable);

    Optional<EditorialResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    // --- LECTURAS ADMIN ---
    Page<EditorialResumen> findAllProjectedBy(Pageable pageable);

    Optional<EditorialResumen> findProjectedById(Long id);

    @Query(value = """
        SELECT e FROM Editorial e
        WHERE
            ( :esBorrado IS NULL OR e.borradoLogico = :esBorrado )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(e.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(e.correo) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(e.personaContacto) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """,
            countQuery = """
        SELECT COUNT(e) FROM Editorial e
        WHERE
            ( :esBorrado IS NULL OR e.borradoLogico = :esBorrado )
            AND (
                :termino IS NULL OR :termino = '' OR
                (
                    LOWER(e.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(e.correo) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                    LOWER(e.personaContacto) LIKE LOWER(CONCAT('%', :termino, '%'))
                )
            )
    """)
    Page<EditorialResumen> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("termino") String termino,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Editorial e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Editorial e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByCorreoIgnoreCase(String correo);

}
