package com.dery.lecatro.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.dery.lecatro.entity.enums.RequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

	@Id
	@SequenceGenerator(name = "request_seq", sequenceName = "request_seq", allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_seq")
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "owner_id")
	private Owner owner;

	@ManyToOne(optional = false)
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "request_status", nullable = false)
	private RequestStatus status;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	private void prePersist() {
		this.publicId = UUID.randomUUID();
		this.createdAt = LocalDateTime.now();
		this.status = RequestStatus.PENDING;
	}
}