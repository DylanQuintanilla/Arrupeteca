package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Nacionalidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NacionalidadRepository extends JpaRepository<Nacionalidad, Long> {

    Page<Nacionalidad> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Nacionalidad> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Nacionalidad> findAllProjectedBy(Pageable pageable);

    Optional<Nacionalidad> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Nacionalidad n SET n.borradoLogico = true WHERE n.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Nacionalidad n SET n.borradoLogico = false WHERE n.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
