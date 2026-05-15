package com.dery.lecatro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dery.lecatro.dto.request.PaymentRequest;
import com.dery.lecatro.dto.response.PaymentResponse;
import com.dery.lecatro.entity.Owner;
import com.dery.lecatro.entity.Payment;
import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.enums.PaymentMethod;
import com.dery.lecatro.entity.enums.PaymentStatus;
import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.exception.BusinessException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.PaymentMapper;
import com.dery.lecatro.repository.PaymentRepository;
import com.dery.lecatro.repository.RequestRepository;
import com.dery.lecatro.service.impl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private RequestRepository requestRepository;

	@Mock
	private PaymentMapper paymentMapper;

	@Mock
	private HistoryService historyService;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	private Request request;
	private Payment payment;
	private PaymentRequest paymentRequest;
	private PaymentResponse paymentResponse;
	private UUID requestPublicId;
	private UUID paymentPublicId;

	@BeforeEach
	void setUp() {
		requestPublicId = UUID.randomUUID();
		paymentPublicId = UUID.randomUUID();

		Owner owner = Owner.builder().email("dery@email.com").firstName("Aderito").build();

		request = Request.builder().id(1L).publicId(requestPublicId).status(RequestStatus.PENDING).owner(owner).build();

		payment = Payment.builder().id(1L).publicId(paymentPublicId).request(request).amount(new BigDecimal("6500.00"))
				.method(PaymentMethod.MPESA).status(PaymentStatus.PENDING).build();

		paymentRequest = new PaymentRequest(requestPublicId, new BigDecimal("6500.00"), PaymentMethod.MPESA);

		paymentResponse = new PaymentResponse(paymentPublicId, new BigDecimal("6500.00"), PaymentMethod.MPESA,
				PaymentStatus.PENDING);
	}

	@Test
	void shouldRegisterPaymentSuccessfully() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));
		when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
		when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

		// Act
		PaymentResponse result = paymentService.create(paymentRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.status()).isEqualTo(PaymentStatus.PENDING);
		verify(paymentRepository, times(1)).save(any(Payment.class));
	}

	@Test
	void shouldThrowExceptionWhenRequestIsNotPending() {
		// Arrange
		request.setStatus(RequestStatus.PAID);
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));

		// Act + Assert
		assertThrows(BusinessException.class, () -> paymentService.create(paymentRequest));
		verify(paymentRepository, never()).save(any());
	}

	@Test
	void shouldThrowExceptionWhenRequestNotFoundOnCreate() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class, () -> paymentService.create(paymentRequest));
	}

	@Test
	void shouldConfirmPaymentAndUpdateRequestStatus() {
		// Arrange
		when(paymentRepository.findByPublicId(paymentPublicId)).thenReturn(Optional.of(payment));
		when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

		// Act
		paymentService.confirm(paymentPublicId);

		// Assert
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CONFIRMED);
		assertThat(request.getStatus()).isEqualTo(RequestStatus.PAID);
		verify(requestRepository, times(1)).save(request);
	}

	@Test
	void shouldThrowExceptionWhenPaymentNotFound() {
		// Arrange
		when(paymentRepository.findByPublicId(paymentPublicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class, () -> paymentService.confirm(paymentPublicId));
	}

	@Test
	void shouldRejectPayment() {
		// Arrange
		when(paymentRepository.findByPublicId(paymentPublicId)).thenReturn(Optional.of(payment));
		when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

		// Act
		paymentService.reject(paymentPublicId);

		// Assert
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REJECTED);
	}
}