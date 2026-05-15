package com.dery.lecatro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dery.lecatro.dto.request.RequestRequest;
import com.dery.lecatro.dto.response.RequestResponse;
import com.dery.lecatro.entity.Owner;
import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.User;
import com.dery.lecatro.entity.Vehicle;
import com.dery.lecatro.entity.enums.HistoryEvent;
import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.entity.enums.Role;
import com.dery.lecatro.exception.BusinessException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.RequestMapper;
import com.dery.lecatro.repository.OwnerRepository;
import com.dery.lecatro.repository.RequestRepository;
import com.dery.lecatro.repository.UserRepository;
import com.dery.lecatro.repository.VehicleRepository;
import com.dery.lecatro.service.impl.RequestServiceImpl;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

	@Mock
	private RequestRepository requestRepository;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private VehicleRepository vehicleRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RequestMapper requestMapper;

	@Mock
	private HistoryService historyService;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private RequestServiceImpl requestService;

	private Request request;
	private RequestRequest requestRequest;
	private RequestResponse requestResponse;
	private Owner owner;
	private Vehicle vehicle;
	private User user;
	private UUID ownerPublicId;
	private UUID vehiclePublicId;
	private UUID requestPublicId;

	@BeforeEach
	void setUp() {
		ownerPublicId = UUID.randomUUID();
		vehiclePublicId = UUID.randomUUID();
		requestPublicId = UUID.randomUUID();

		owner = Owner.builder().id(1L).publicId(ownerPublicId).firstName("Aderito").lastName("Andala")
				.email("dery@email.com").build();

		vehicle = Vehicle.builder().id(1L).publicId(vehiclePublicId).brand("Toyota").model("Corolla").build();

		user = User.builder().id(1L).email("operador@lecatro.mz").province(Province.MAPUTO_CITY).role(Role.OPERATOR)
				.build();

		request = Request.builder().id(1L).publicId(requestPublicId).owner(owner).vehicle(vehicle).user(user)
				.status(RequestStatus.PENDING).build();

		requestRequest = new RequestRequest(ownerPublicId, vehiclePublicId);

		requestResponse = mock(RequestResponse.class);

		// mock do SecurityContextHolder — simula utilizador autenticado
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
		lenient().when(authentication.getName()).thenReturn("operador@lecatro.mz");
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	void shouldCreateRequestSuccessfully() {
		// Arrange
		when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
		when(vehicleRepository.findByPublicId(vehiclePublicId)).thenReturn(Optional.of(vehicle));
		when(userRepository.findByEmail("operador@lecatro.mz")).thenReturn(Optional.of(user));
		when(requestRepository.save(any(Request.class))).thenReturn(request);
		when(requestMapper.toResponse(request)).thenReturn(requestResponse);

		// Act
		RequestResponse result = requestService.create(requestRequest);

		// Assert
		assertThat(result).isNotNull();
		verify(requestRepository, times(1)).save(any(Request.class));

		verify(emailService, times(1)).sendRequestStatusNotification(anyString(), anyString(), anyString(),
				eq(RequestStatus.PENDING));

		verify(historyService, times(1)).record(eq(requestPublicId), eq(HistoryEvent.REGISTRATION), anyString());
	}

	@Test
	void shouldThrowExceptionWhenOwnerNotFound() {
		// Arrange
		when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class, () -> requestService.create(requestRequest));
		verify(requestRepository, never()).save(any());
	}

	@Test
	void shouldThrowExceptionWhenVehicleNotFound() {
		// Arrange
		when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
		when(vehicleRepository.findByPublicId(vehiclePublicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class, () -> requestService.create(requestRequest));
		verify(requestRepository, never()).save(any());
	}

	@Test
	void shouldListAllRequests() {
		// Arrange
		when(requestRepository.findAll()).thenReturn(List.of(request));
		when(requestMapper.toResponse(request)).thenReturn(requestResponse);

		// Act
		List<RequestResponse> result = requestService.findAll();

		// Assert
		assertThat(result).hasSize(1);
	}

	@Test
	void shouldFindRequestByPublicId() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));
		when(requestMapper.toResponse(request)).thenReturn(requestResponse);

		// Act
		RequestResponse result = requestService.findByPublicId(requestPublicId);

		// Assert
		assertThat(result).isNotNull();
	}

	@Test
	void shouldCancelRequestSuccessfully() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));
		when(requestRepository.save(request)).thenReturn(request);
		when(requestMapper.toResponse(request)).thenReturn(requestResponse);

		// Act
		requestService.cancel(requestPublicId);

		// Assert
		assertThat(request.getStatus()).isEqualTo(RequestStatus.CANCELLED);
		verify(historyService, times(1)).record(eq(requestPublicId), eq(HistoryEvent.CANCELLATION), anyString());
	}

	@Test
	void shouldThrowExceptionWhenCancellingIssuedRequest() {
		// Arrange
		request.setStatus(RequestStatus.ISSUED);
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));

		// Act + Assert
		assertThrows(BusinessException.class, () -> requestService.cancel(requestPublicId));
		verify(requestRepository, never()).save(any());
	}
}