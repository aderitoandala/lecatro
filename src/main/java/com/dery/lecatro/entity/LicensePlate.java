package com.dery.lecatro.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.dery.lecatro.entity.enums.LicensePlateStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "license_plate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicensePlate {

	@Id
	@SequenceGenerator(name = "license_plate_seq", sequenceName = "license_plate_seq", allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "license_plate_seq")
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@OneToOne(optional = false)
	@JoinColumn(name = "request_id")
	private Request request;

	@Column(name = "plate_number", nullable = false, unique = true)
	private String number;

	@Column(nullable = false)
	private LocalDate issueDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "plate_status", nullable = false)
	private LicensePlateStatus status;

	@PrePersist
	private void prePersist() {
		this.publicId = UUID.randomUUID();

	}
}