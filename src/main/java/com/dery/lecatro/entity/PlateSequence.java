package com.dery.lecatro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plate_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlateSequence {

	@Id
	@Column(name = "province_code")
	private String provinceCode;

	@Column(name = "last_index", nullable = false)
	private Long lastIndex = -1L;
}