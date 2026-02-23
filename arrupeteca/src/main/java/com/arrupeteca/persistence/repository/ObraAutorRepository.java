package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.ObraAutor;
import com.arrupeteca.persistence.projection.ObraAutorResumen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObraAutorRepository extends JpaRepository<ObraAutor, Long> {


    //  Admin

    @EntityGraph(attributePaths = {"autor", "tipoAutoria"})
    List<ObraAutorResumen> findByObraIdAndBorradoLogicoFalse(Long idObra);

    @EntityGraph(attributePaths = {"autor", "tipoAutoria"})
    List<ObraAutorResumen> findByObraId(Long idObra);

    @Modifying
    @Query("UPDATE ObraAutor oa SET oa.borradoLogico = true WHERE oa.id = :id")
    void desactivarObraAutor(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ObraAutor oa SET oa.borradoLogico = false WHERE oa.id = :id")
    void activarObraAutor(@Param("id") Long id);

}
