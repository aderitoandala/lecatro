package com.dery.lecatro.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dery.lecatro.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	Optional<Vehicle> findByChassisNumber(String chassisNumber);

	Optional<Vehicle> findByPublicId(UUID publicId);

	boolean existsByChassisNumber(String chassisNumber);

	Page<Vehicle> findAll(Pageable pageable);

	@Query("SELECT v FROM Vehicle v WHERE " + "LOWER(v.brand)      LIKE %:search% OR "
			+ "LOWER(v.model)      LIKE %:search% OR " + "v.chassisNumber     LIKE %:search%")
	Page<Vehicle> findBySearch(@Param("search") String search, Pageable pageable);
}