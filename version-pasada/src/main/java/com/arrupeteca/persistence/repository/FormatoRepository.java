package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Formato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormatoRepository extends JpaRepository<Formato, Long> {

    Page<Formato> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Formato> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Formato> findAllProjectedBy(Pageable pageable);

    Optional<Formato> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Formato f SET f.borradoLogico = true WHERE f.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Formato f SET f.borradoLogico = false WHERE f.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
