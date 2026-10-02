package com.arrupeteca.transferencia.internal.repository;

import com.arrupeteca.transferencia.internal.entity.EjemplarTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EjemplarTransferenciaRepository extends JpaRepository<EjemplarTransferencia, Long> {

    @Query("SELECT e FROM EjemplarTransferencia e JOIN FETCH e.ejemplarLibro WHERE e.transferencia.id = :idTransferencia")
    List<EjemplarTransferencia> findByTransferenciaId(@Param("idTransferencia") Long idTransferencia);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM EjemplarTransferencia e WHERE e.transferencia.id = :idTransferencia")
    void deleteByTransferenciaId(@Param("idTransferencia") Long idTransferencia);
}
