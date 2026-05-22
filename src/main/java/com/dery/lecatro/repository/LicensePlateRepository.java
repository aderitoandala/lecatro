package com.dery.lecatro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dery.lecatro.entity.LicensePlate;
import com.dery.lecatro.entity.enums.LicensePlateStatus;

public interface LicensePlateRepository extends JpaRepository<LicensePlate, Long> {

	Optional<LicensePlate> findByNumber(String number);

	Optional<LicensePlate> findByPublicId(UUID publicId);

	List<LicensePlate> findByStatusOrderByIdDesc(LicensePlateStatus status);

	boolean existsByRequestVehicleIdAndStatus(Long vehicleId, LicensePlateStatus status);

	boolean existsByNumber(String number);

	List<LicensePlate> findBy(Pageable pageable);

	Optional<LicensePlate> findByRequestPublicId(UUID requestPublicId);

}