package com.dery.lecatro.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.enums.RequestStatus;

public interface RequestRepository extends JpaRepository<Request, Long> {

	Optional<Request> findByPublicId(UUID publicId);

	List<Request> findByStatus(RequestStatus status);

	List<Request> findByVehicleId(Long vehicleId);

	List<Request> findByOwnerId(Long ownerId);

	@Query("SELECT r FROM Request r WHERE YEAR(r.createdAt) = :year")
	List<Request> findByYear(@Param("year") int year);

	@Query("SELECT r FROM Request r WHERE YEAR(r.createdAt) = :year AND MONTH(r.createdAt) = :month")
	List<Request> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

	List<Request> findByStatusIn(List<RequestStatus> statuses);

	List<Request> findByCreatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

}