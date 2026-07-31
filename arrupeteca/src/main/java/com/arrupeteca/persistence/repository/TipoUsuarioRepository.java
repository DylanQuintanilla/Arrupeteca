package com.arrupeteca.persistence.repository;

import java.util.Optional;

import com.arrupeteca.persistence.entity.TipoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    Page<TipoUsuario> findByBorradoLogicoFalse(Pageable pageable);

    Optional<TipoUsuario> findByIdAndBorradoLogicoFalse(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE TipoUsuario t SET t.borradoLogico = true WHERE t.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE TipoUsuario t SET t.borradoLogico = false WHERE t.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM tipo_usuario WHERE id_tipo = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<TipoUsuario> findByNombreAndBorradoLogicoFalse(String nombre);

}
