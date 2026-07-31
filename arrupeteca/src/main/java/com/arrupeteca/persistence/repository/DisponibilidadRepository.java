package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Disponibilidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    List<Disponibilidad> findByBorradoLogicoFalseOrderByNombreAsc();

    @Query("SELECT d FROM Disponibilidad d WHERE " +
           "(:nombre IS NULL OR LOWER(d.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:borradoLogico IS NULL OR d.borradoLogico = :borradoLogico)")
    Page<Disponibilidad> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Disponibilidad d SET d.borradoLogico = true WHERE d.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Disponibilidad d SET d.borradoLogico = false WHERE d.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM disponibilidad WHERE id_disponibilidad = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
