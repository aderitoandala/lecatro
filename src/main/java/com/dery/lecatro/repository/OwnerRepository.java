package com.dery.lecatro.repository;

import com.dery.lecatro.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

	Optional<Owner> findByNuit(String nuit);

	Optional<Owner> findByPublicId(UUID publicId);
}