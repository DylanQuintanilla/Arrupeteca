package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.projection.MuebleResumen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MuebleRepository extends JpaRepository<Mueble, Long> {

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    List<MuebleResumen> findByBorradoLogicoFalse();

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    Optional<MuebleResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    List<MuebleResumen> findProjectedBySalon_IdAndBorradoLogicoFalse(Long idSalon);

    @EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
    List<MuebleResumen> findAllProjectedBy();

    @Modifying
    @Query("UPDATE Mueble m SET m.borradoLogico = true WHERE m.id = :id")
    void desactivarMueble(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Mueble m SET m.borradoLogico = false WHERE m.id = :id")
    void activarMueble(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    // Verifica si ya existe un mueble con ese nombre en ESE salón
    boolean existsByNombreIgnoreCaseAndSalon_Id(String nombre, Long idSalon);

}
