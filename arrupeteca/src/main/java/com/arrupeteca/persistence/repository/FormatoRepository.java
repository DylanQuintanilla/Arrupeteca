package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Formato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormatoRepository extends JpaRepository<Formato, Long>{
}
