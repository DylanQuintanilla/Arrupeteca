package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.TipoAutoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoAutoriaRepository extends JpaRepository<TipoAutoria, Long> {

    Page<TipoAutoria> findByBorradoLogicoFalse(Pageable pageable);

    Optional<TipoAutoria> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<TipoAutoria> findAllProjectedBy(Pageable pageable);

    Optional<TipoAutoria> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE TipoAutoria t SET t.borradoLogico = true WHERE t.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE TipoAutoria t SET t.borradoLogico = false WHERE t.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
