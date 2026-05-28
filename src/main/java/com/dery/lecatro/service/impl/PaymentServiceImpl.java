package com.dery.lecatro.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dery.lecatro.dto.request.PaymentRequest;
import com.dery.lecatro.dto.response.PaymentResponse;
import com.dery.lecatro.entity.Payment;
import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.enums.HistoryEvent;
import com.dery.lecatro.entity.enums.PaymentStatus;
import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.exception.BusinessException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.PaymentMapper;
import com.dery.lecatro.repository.PaymentRepository;
import com.dery.lecatro.repository.RequestRepository;
import com.dery.lecatro.service.EmailService;
import com.dery.lecatro.service.HistoryService;
import com.dery.lecatro.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final RequestRepository requestRepository;
	private final PaymentMapper paymentMapper;
	private final HistoryService historyService;
	private final EmailService emailService;

	@Override
	@Transactional
	public PaymentResponse create(PaymentRequest request) {

		Request existingRequest = requestRepository.findByPublicId(request.requestPublicId())
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

		// só é possível registar pagamentos com pedidos pendentes
		if (existingRequest.getStatus() != RequestStatus.PENDING) {
			throw new BusinessException("O pedido não está em estado pendente");
		}

		// cria o pagamento com estado pendente
		Payment payment = Payment.builder().request(existingRequest).amount(request.amount()).method(request.method())
				.status(PaymentStatus.PENDING).build();

		Payment saved = paymentRepository.save(payment);

		// regista o evento de pagamento no histórico
		historyService.record(existingRequest.getPublicId(), HistoryEvent.PAYMENT, "Pagamento registado");

		return paymentMapper.toResponse(saved);
	}

	@Override
	@Transactional
	public PaymentResponse confirm(UUID publicId) {
		Payment payment = paymentRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));

		// confirma o pagamento
		payment.setStatus(PaymentStatus.CONFIRMED);
		Payment saved = paymentRepository.save(payment);

		// actualiza o estado do pedido para PAID
		Request request = payment.getRequest();
		request.setStatus(RequestStatus.PAID);
		requestRepository.save(request);

		// regista confirmação no histórico
		historyService.record(request.getPublicId(), HistoryEvent.PAYMENT,
				"Pagamento confirmado:" + " " + saved.getAmount());

		// notifica o proprietário que o pagamento foi confirmado
		emailService.sendRequestStatusNotification(request.getOwner().getEmail(), request.getOwner().getFirstName(),
				request.getPublicId().toString(), RequestStatus.PAID);

		return paymentMapper.toResponse(payment);
	}

	@Override
	@Transactional
	public PaymentResponse reject(UUID publicId) {
		Payment payment = paymentRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));

		payment.setStatus(PaymentStatus.REJECTED);
		paymentRepository.save(payment);

		// regista rejeição no histórico
		historyService.record(payment.getRequest().getPublicId(), HistoryEvent.PAYMENT, "Pagamento rejeitado");

		return paymentMapper.toResponse(payment);
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentResponse findByRequest(UUID requestPublicId) {
		Request request = requestRepository.findByPublicId(requestPublicId)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

		return paymentRepository.findByRequestId(request.getId()).map(paymentMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));
	}
}