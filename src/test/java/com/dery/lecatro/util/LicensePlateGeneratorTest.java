package com.dery.lecatro.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.repository.LicensePlateRepository;

@ExtendWith(MockitoExtension.class)
class LicensePlateGeneratorTest {

	@Mock
	private LicensePlateRepository licensePlateRepository;

	@InjectMocks
	private LicensePlateGenerator generator;

	@BeforeEach
	void setUp() {
		// by default — no number exists in the database
		when(licensePlateRepository.existsByNumber(anyString())).thenReturn(false);
	}

	@Test
	void shouldGenerateLicensePlateWithCorrectFormat() {
		// Act
		String plate = generator.generate(Province.MAPUTO_CITY);

		// Assert — format: ABC 123 MC
		assertThat(plate).matches("[A-Z]{3} \\d{3} [A-Z]{2}");
	}

	@Test
	void shouldEndWithMaputoCityProvinceCode() {
		// Act
		String plate = generator.generate(Province.MAPUTO_CITY);

		// Assert
		assertThat(plate).endsWith("MC");
	}

	@Test
	void shouldEndWithGazaProvinceCode() {
		// Act
		String plate = generator.generate(Province.GAZA);

		// Assert
		assertThat(plate).endsWith("GZ");
	}

	@Test
	void shouldRetryWhenGeneratedNumberAlreadyExists() {
		// Arrange — first number exists, second is unique
		when(licensePlateRepository.existsByNumber(anyString())).thenReturn(true).thenReturn(false);

		// Act
		String plate = generator.generate(Province.MAPUTO_CITY);

		// Assert — valid plate generated despite collision
		assertThat(plate).matches("[A-Z]{3} \\d{3} [A-Z]{2}");
	}

	@Test
	void shouldGenerateLicensePlateWithCorrectLength() {
		// format "ABC 123 MC" has exactly 10 characters
		String plate = generator.generate(Province.MAPUTO_CITY);
		assertThat(plate).hasSize(10);
	}
}