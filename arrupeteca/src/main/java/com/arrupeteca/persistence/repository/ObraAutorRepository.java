package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.ObraAutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ObraAutorRepository extends JpaRepository<ObraAutor, Long> {

    @EntityGraph(attributePaths = {"autor", "tipoAutoria"})
    @Query("SELECT oa FROM ObraAutor oa WHERE oa.obra.id = :idObra AND oa.borradoLogico = false")
    Page<ObraAutor> buscarPorObraIdYActivo(@Param("idObra") Long idObra, Pageable pageable);

    @EntityGraph(attributePaths = {"autor", "tipoAutoria"})
    @Query("SELECT oa FROM ObraAutor oa WHERE oa.obra.id = :idObra")
    Page<ObraAutor> buscarPorObraId(@Param("idObra") Long idObra, Pageable pageable);

    boolean existsByObraIdAndAutorIdAndTipoAutoriaId(Long idObra, Long idAutor, Long idTipoAutoria);

    Optional<ObraAutor> findByObraIdAndAutorIdAndTipoAutoriaId(Long idObra, Long idAutor, Long idTipoAutoria);

}
