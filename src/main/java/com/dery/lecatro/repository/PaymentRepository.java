package com.dery.lecatro.repository;

import com.dery.lecatro.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByRequestId(Long requestId);

	Optional<Payment> findByPublicId(UUID publicId);
}