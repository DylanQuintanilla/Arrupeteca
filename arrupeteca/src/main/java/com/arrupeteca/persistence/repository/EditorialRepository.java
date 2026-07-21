package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Editorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EditorialRepository extends JpaRepository<Editorial, Long> {

    Page<Editorial> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Editorial> findByIdAndBorradoLogicoFalse(Long id);

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
    Page<Editorial> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("termino") String termino,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Editorial e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Editorial e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM editorial WHERE id_editorial = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByCorreoIgnoreCase(String correo);
}
