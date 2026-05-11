package com.dery.lecatro.entity;

import java.time.LocalDate;
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
@Table(name = "owner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {

	@Id
	@SequenceGenerator(name = "owner_seq", sequenceName = "owner_seq", allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "owner_seq")
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false, unique = true)
	private String nuit;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private LocalDate birthDate;

	@PrePersist
	private void prePersist() {
		this.publicId = UUID.randomUUID();
	}
}