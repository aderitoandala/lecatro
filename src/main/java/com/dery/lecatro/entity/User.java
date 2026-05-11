package com.dery.lecatro.entity;

import java.util.UUID;

import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.entity.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "user_password", nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Province province;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false)
	private Role role;

	@PrePersist
	private void prePersist() {
		this.publicId = UUID.randomUUID();
	}
}