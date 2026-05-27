package com.dery.lecatro.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dery.lecatro.entity.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

	Optional<Owner> findByNuit(String nuit);

	Optional<Owner> findByPublicId(UUID publicId);

	boolean existsByNuit(String nuit);

	@Query("SELECT o FROM Owner o WHERE " + "LOWER(o.firstName) LIKE %:search% OR "
			+ "LOWER(o.lastName)  LIKE %:search% OR " + "o.nuit             LIKE %:search%")
	Page<Owner> findBySearch(@Param("search") String search, Pageable pageable);
}