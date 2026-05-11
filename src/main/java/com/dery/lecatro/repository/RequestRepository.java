package com.dery.lecatro.repository;

import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, Long> {

	Optional<Request> findByPublicId(UUID publicId);

	List<Request> findByStatus(RequestStatus status);

	List<Request> findByVehicleId(Long vehicleId);

	List<Request> findByOwnerId(Long ownerId);
}