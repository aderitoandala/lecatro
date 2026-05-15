package com.dery.lecatro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dery.lecatro.dto.response.LicensePlateResponse;
import com.dery.lecatro.entity.LicensePlate;
import com.dery.lecatro.entity.Owner;
import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.User;
import com.dery.lecatro.entity.Vehicle;
import com.dery.lecatro.entity.enums.HistoryEvent;
import com.dery.lecatro.entity.enums.LicensePlateStatus;
import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.exception.BusinessException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.LicensePlateMapper;
import com.dery.lecatro.repository.LicensePlateRepository;
import com.dery.lecatro.repository.RequestRepository;
import com.dery.lecatro.service.impl.LicensePlateServiceImpl;
import com.dery.lecatro.util.LicensePlateGenerator;

@ExtendWith(MockitoExtension.class)
class LicensePlateServiceImplTest {

	@Mock
	private LicensePlateRepository licensePlateRepository;

	@Mock
	private RequestRepository requestRepository;

	@Mock
	private LicensePlateMapper licensePlateMapper;

	@Mock
	private LicensePlateGenerator licensePlateGenerator;

	@Mock
	private HistoryService historyService;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private LicensePlateServiceImpl licensePlateService;

	private Request request;
	private UUID requestPublicId;
	private UUID licensePlatePublicId;
	private Vehicle vehicle;
	private LicensePlate licensePlate;

	@BeforeEach
	void setUp() {
		requestPublicId = UUID.randomUUID();
		licensePlatePublicId = UUID.randomUUID();

		User user = User.builder().province(Province.MAPUTO_CITY).build();

		vehicle = Vehicle.builder().id(1L).build();

		Owner owner = Owner.builder().email("dery@email.com").firstName("Aderito").build();

		request = Request.builder().id(1L).publicId(requestPublicId).status(RequestStatus.PAID).user(user)
				.vehicle(vehicle).owner(owner).build();

		licensePlate = LicensePlate.builder().id(1L).publicId(licensePlatePublicId).request(request)
				.number("ABC 123 MC").issueDate(LocalDate.now()).status(LicensePlateStatus.ACTIVE).build();
	}

	@Test
	void shouldIssueLicensePlateSuccessfully() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));
		when(licensePlateRepository.existsByRequestVehicleIdAndStatus(1L, LicensePlateStatus.ACTIVE)).thenReturn(false);
		when(licensePlateGenerator.generate(Province.MAPUTO_CITY)).thenReturn("ABC 123 MC");
		when(licensePlateRepository.save(any(LicensePlate.class))).thenAnswer(i -> i.getArgument(0));
		when(licensePlateMapper.toResponse(any(LicensePlate.class))).thenReturn(mock(LicensePlateResponse.class));

		// Act
		licensePlateService.issue(requestPublicId);

		// Assert — request moved to ISSUED
		assertThat(request.getStatus()).isEqualTo(RequestStatus.ISSUED);
		verify(licensePlateRepository, times(1)).save(any(LicensePlate.class));

		// Verifica se o serviço de e-mail foi chamado pelo menos uma vez
		verify(emailService, times(1)).sendRequestStatusNotification(anyString(), anyString(), anyString(),
				eq(RequestStatus.ISSUED));
	}

	@Test
	void shouldThrowExceptionWhenRequestIsNotPaid() {
		// Arrange
		request.setStatus(RequestStatus.PENDING);
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));

		// Act + Assert
		assertThrows(BusinessException.class, () -> licensePlateService.issue(requestPublicId));
		verify(licensePlateRepository, never()).save(any());
	}

	@Test
	void shouldThrowExceptionWhenVehicleAlreadyHasActiveLicensePlate() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));
		when(licensePlateRepository.existsByRequestVehicleIdAndStatus(1L, LicensePlateStatus.ACTIVE)).thenReturn(true);

		// Act + Assert
		assertThrows(BusinessException.class, () -> licensePlateService.issue(requestPublicId));
		verify(licensePlateRepository, never()).save(any());
	}

	@Test
	void shouldThrowExceptionWhenRequestNotFound() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class, () -> licensePlateService.issue(requestPublicId));
	}

	@Test
	void shouldFindLicensePlateByNumber() {
		// Arrange
		when(licensePlateRepository.findByNumber("ABC 123 MC")).thenReturn(Optional.of(licensePlate));
		when(licensePlateMapper.toResponse(licensePlate)).thenReturn(mock(LicensePlateResponse.class));

		// Act
		LicensePlateResponse result = licensePlateService.findByNumber("ABC 123 MC");

		// Assert
		assertThat(result).isNotNull();
	}
	
	 @Test
	    void shouldThrowExceptionWhenLicensePlateNotFoundByNumber() {
	        // Arrange
	        when(licensePlateRepository.findByNumber("XYZ 999 CA")).thenReturn(Optional.empty());

	        // Act + Assert
	        assertThrows(ResourceNotFoundException.class, () -> licensePlateService.findByNumber("XYZ 999 CA"));
	    }

	@Test
	void shouldCancelLicensePlateSuccessfully() {
		// Arrange
		when(licensePlateRepository.findByPublicId(licensePlatePublicId)).thenReturn(Optional.of(licensePlate));
		when(licensePlateRepository.save(licensePlate)).thenReturn(licensePlate);
		when(licensePlateMapper.toResponse(licensePlate)).thenReturn(mock(LicensePlateResponse.class));

		// Act
		licensePlateService.cancel(licensePlatePublicId);

		// Assert
		assertThat(licensePlate.getStatus()).isEqualTo(LicensePlateStatus.CANCELLED);
		verify(historyService, times(1)).record(eq(requestPublicId), eq(HistoryEvent.CANCELLATION), anyString());
	}

}