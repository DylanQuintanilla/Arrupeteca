package com.arrupeteca.persistence.repository;

import java.util.Optional;

import com.arrupeteca.persistence.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoAndBorradoLogicoFalse(String correo);

    boolean existsByCorreo(String correo);

    Page<Usuario> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Usuario> findByIdAndBorradoLogicoFalse(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Usuario u SET u.borradoLogico = true WHERE u.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Usuario u SET u.borradoLogico = false WHERE u.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM usuario WHERE id_usuario = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

}
