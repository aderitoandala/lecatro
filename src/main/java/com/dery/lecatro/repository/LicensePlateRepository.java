package com.dery.lecatro.repository;

import com.dery.lecatro.entity.LicensePlate;
import com.dery.lecatro.entity.enums.LicensePlateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LicensePlateRepository extends JpaRepository<LicensePlate, Long> {

	Optional<LicensePlate> findByNumber(String number);

	Optional<LicensePlate> findByPublicId(UUID publicId);

	boolean existsByRequestVehicleIdAndStatus(Long vehicleId, LicensePlateStatus status);

	boolean existsByNumber(String number);

}