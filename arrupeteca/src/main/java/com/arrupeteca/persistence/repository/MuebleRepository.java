package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Mueble;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MuebleRepository extends JpaRepository<Mueble, Long> {

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    Page<Mueble> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    Optional<Mueble> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    @Query(value = "SELECT m FROM Mueble m WHERE m.borradoLogico = false " +
            "AND (:palabra IS NULL OR :palabra = '' OR " +
            "LOWER(m.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')))")
    Page<Mueble> busquedaGlobal(@Param("palabra") String palabra, Pageable pageable);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    Page<Mueble> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    Optional<Mueble> findById(Long id);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    @Query(value = "SELECT m FROM Mueble m WHERE " +
            "(:esBorrado IS NULL OR m.borradoLogico = :esBorrado) " +
            "AND (:idSalon IS NULL OR m.salon.id = :idSalon) " +
            "AND (:termino IS NULL OR :termino = '' OR " +
            "LOWER(m.nombre) LIKE LOWER(CONCAT('%', :termino, '%')))")
    Page<Mueble> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idSalon") Long idSalon,
            @Param("termino") String termino,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Mueble m SET m.borradoLogico = true WHERE m.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Mueble m SET m.borradoLogico = false WHERE m.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM mueble WHERE id_mueble = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCaseAndSalonId(String nombre, Long idSalon);
}
