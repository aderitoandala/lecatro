package com.dery.lecatro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dery.lecatro.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	Optional<Vehicle> findByChassisNumber(String chassisNumber);

	Optional<Vehicle> findByPublicId(UUID publicId);

	boolean existsByChassisNumber(String chassisNumber);

	@Query("SELECT v FROM Vehicle v WHERE " + "LOWER(v.brand)         LIKE %:search% OR "
			+ "LOWER(v.model)         LIKE %:search% OR " + "LOWER(v.chassisNumber)        LIKE %:search%")
	List<Vehicle> findBySearch(@Param("search") String search);
}