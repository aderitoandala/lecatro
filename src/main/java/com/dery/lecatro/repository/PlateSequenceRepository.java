package com.dery.lecatro.repository;

import com.dery.lecatro.entity.PlateSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface PlateSequenceRepository extends JpaRepository<PlateSequence, String> {

    
    // Bloqueio pessimista:dois pedidos simultaneos nunca geram o mesmo número de matrícula
    @Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT ps FROM PlateSequence ps WHERE ps.provinceCode = :provinceCode")
	Optional<PlateSequence> findByProvinceForUpdate(String provinceCode);
}