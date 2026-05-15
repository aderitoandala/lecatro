package com.dery.lecatro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dery.lecatro.dto.request.VehicleRequest;
import com.dery.lecatro.dto.response.VehicleResponse;
import com.dery.lecatro.entity.Vehicle;
import com.dery.lecatro.exception.DataIntegrityException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.VehicleMapper;
import com.dery.lecatro.repository.VehicleRepository;
import com.dery.lecatro.service.impl.VehicleServiceImpl;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

	@Mock
	private VehicleRepository vehicleRepository;

	@Mock
	private VehicleMapper vehicleMapper;

	@InjectMocks
	private VehicleServiceImpl vehicleService;

	private Vehicle vehicle;
	private VehicleRequest vehicleRequest;
	private VehicleResponse vehicleResponse;
	private UUID publicId;

	@BeforeEach
	void setUp() {
		publicId = UUID.randomUUID();

		vehicle = Vehicle.builder().id(1L).publicId(publicId).brand("Toyota").model("Corolla").color("Branco")
				.chassisNumber("ABC12345622334455").manufactureYear(2020).build();

		vehicleRequest = new VehicleRequest("Toyota", "Corolla", "Branco", "ABC12345622334455", 2020);
		vehicleResponse = new VehicleResponse(publicId, "Toyota", "Corolla", "Branco", "ABC12345622334455", 2020);
	}

	@Test
	void shouldCreateVehicleSuccessfully() {
		// Arrange
		when(vehicleRepository.existsByChassisNumber(vehicleRequest.chassisNumber())).thenReturn(false);
		when(vehicleMapper.toEntity(vehicleRequest)).thenReturn(vehicle);
		when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
		when(vehicleMapper.toResponse(vehicle)).thenReturn(vehicleResponse);

		// Act
		VehicleResponse result = vehicleService.create(vehicleRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.chassisNumber()).isEqualTo("ABC12345622334455");
		verify(vehicleRepository, times(1)).save(vehicle);
	}

	@Test
	void shouldListAllVehicles() {
		// Arrange
		when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
		when(vehicleMapper.toResponse(vehicle)).thenReturn(vehicleResponse);

		// Act
		List<VehicleResponse> result = vehicleService.findAll();

		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.get(0).chassisNumber()).isEqualTo("ABC12345622334455");
	}

	@Test
	void shouldUpdateVehicleSuccessfully() {
		// Arrange
		when(vehicleRepository.findByPublicId(publicId)).thenReturn(Optional.of(vehicle));
		when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
		when(vehicleMapper.toResponse(vehicle)).thenReturn(vehicleResponse);

		// Act
		VehicleResponse result = vehicleService.update(publicId, vehicleRequest);

		// Assert
		assertThat(result).isNotNull();
		verify(vehicleRepository, times(1)).save(vehicle);
	}

	@Test
	void shouldFindVehicleByPublicId() {
		// Arrange
		when(vehicleRepository.findByPublicId(publicId)).thenReturn(Optional.of(vehicle));
		when(vehicleMapper.toResponse(vehicle)).thenReturn(vehicleResponse);

		// Act
		VehicleResponse result = vehicleService.findByPublicId(publicId);

		// Assert
		assertThat(result.publicId()).isEqualTo(publicId);
	}

	@Test
	void shouldThrowExceptionWhenChassisNumberAlreadyExists() {
		// Arrange
		when(vehicleRepository.existsByChassisNumber(vehicleRequest.chassisNumber())).thenReturn(true);

		// Act + Assert
		assertThrows(DataIntegrityException.class, () -> vehicleService.create(vehicleRequest));
		verify(vehicleRepository, never()).save(any());
	}

	@Test
	void shouldThrowExceptionWhenVehicleNotFound() {
		// Arrange
		when(vehicleRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class, () -> vehicleService.findByPublicId(publicId));
	}

	@Test
	void shouldDeleteVehicle() {
		// Arrange
		when(vehicleRepository.findByPublicId(publicId)).thenReturn(Optional.of(vehicle));

		// Act
		vehicleService.delete(publicId);

		// Assert
		verify(vehicleRepository, times(1)).delete(vehicle);
	}
}