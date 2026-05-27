package com.dery.lecatro.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.enums.RequestStatus;

public interface RequestRepository extends JpaRepository<Request, Long> {

	Optional<Request> findByPublicId(UUID publicId);

	boolean existsByVehicleIdAndStatusIn(Long vehicleId, List<RequestStatus> statuses);

	@Query("SELECT r FROM Request r WHERE YEAR(r.createdAt) = :year")
	List<Request> findByYear(@Param("year") int year);

	Page<Request> findByStatus(RequestStatus status, Pageable pageable);

	Page<Request> findByVehicleId(Long vehicleId, Pageable pageable);

	Page<Request> findByOwnerId(Long ownerId, Pageable pageable);

	@Query("SELECT r FROM Request r WHERE YEAR(r.createdAt) = :year")
	Page<Request> findByYear(@Param("year") int year, Pageable pageable);

	@Query("SELECT r FROM Request r WHERE YEAR(r.createdAt) = :year AND MONTH(r.createdAt) = :month")
	Page<Request> findByYearAndMonth(@Param("year") int year, @Param("month") int month, Pageable pageable);

	Page<Request> findByStatusIn(List<RequestStatus> statuses, Pageable pageable);

	Page<Request> findByCreatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
}
