package com.dery.lecatro.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

	@Id
	@SequenceGenerator(name = "vehicle_seq", sequenceName = "vehicle_seq", allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_seq")
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(nullable = false)
	private String brand;

	@Column(nullable = false)
	private String model;

	@Column(nullable = false)
	private String color;

	@Column(nullable = false, unique = true)
	private String chassisNumber;

	@Column(nullable = false)
	private Integer manufactureYear;

	@PrePersist
	private void prePersist() {
		this.publicId = UUID.randomUUID();
	}
}