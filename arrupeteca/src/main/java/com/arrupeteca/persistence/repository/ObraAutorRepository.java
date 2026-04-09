package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.ObraAutor;
import com.arrupeteca.persistence.projection.ObraAutorResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ObraAutorRepository extends JpaRepository<ObraAutor, Long> {

    @EntityGraph(attributePaths = {"autor", "tipoAutoria"})
    @Query("SELECT oa FROM ObraAutor oa WHERE oa.obra.id = :idObra AND oa.borradoLogico = false")
    Page<ObraAutorResumen> buscarPorObraIdYActivo(@Param("idObra") Long idObra, Pageable pageable);

    @EntityGraph(attributePaths = {"autor", "tipoAutoria"})
    @Query("SELECT oa FROM ObraAutor oa WHERE oa.obra.id = :idObra")
    Page<ObraAutorResumen> buscarPorObraId(@Param("idObra") Long idObra, Pageable pageable);

    @Modifying
    @Query("UPDATE ObraAutor oa SET oa.borradoLogico = true WHERE oa.id = :id")
    void desactivarObraAutor(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ObraAutor oa SET oa.borradoLogico = false WHERE oa.id = :id")
    void activarObraAutor(@Param("id") Long id);

}
