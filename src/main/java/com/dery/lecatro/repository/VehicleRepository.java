package com.dery.lecatro.repository;

import com.dery.lecatro.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	Optional<Vehicle> findByChassisNumber(String chassisNumber);

	Optional<Vehicle> findByPublicId(UUID publicId);

	boolean existsByChassisNumber(String chassisNumber);
}