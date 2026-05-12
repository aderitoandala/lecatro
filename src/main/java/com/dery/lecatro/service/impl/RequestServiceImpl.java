package com.dery.lecatro.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dery.lecatro.dto.request.RequestRequest;
import com.dery.lecatro.dto.response.RequestResponse;
import com.dery.lecatro.entity.Owner;
import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.User;
import com.dery.lecatro.entity.Vehicle;
import com.dery.lecatro.entity.enums.HistoryEvent;
import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.mapper.RequestMapper;
import com.dery.lecatro.repository.OwnerRepository;
import com.dery.lecatro.repository.RequestRepository;
import com.dery.lecatro.repository.UserRepository;
import com.dery.lecatro.repository.VehicleRepository;
import com.dery.lecatro.service.EmailService;
import com.dery.lecatro.service.HistoryService;
import com.dery.lecatro.service.RequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

	private final RequestRepository requestRepository;
	private final OwnerRepository ownerRepository;
	private final VehicleRepository vehicleRepository;
	private final UserRepository userRepository;
	private final RequestMapper requestMapper;
	private final HistoryService historyService;
	private final EmailService emailService;

	@Override
	@Transactional
	public RequestResponse create(RequestRequest request) {

		Owner owner = ownerRepository.findByPublicId(request.ownerPublicId())
				.orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));

		Vehicle vehicle = vehicleRepository.findByPublicId(request.vehiclePublicId())
				.orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

		// obtem o utilizador autenticado que esta a processar o pedido
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Utilizador autenticado não encontrado"));

		// cria um novo pedido com status PENDING
		Request newRequest = Request.builder().owner(owner).vehicle(vehicle).user(user).build();

		Request saved = requestRepository.save(newRequest);

		// regista o evento de criaçao no histórico
		historyService.record(saved.getPublicId(), HistoryEvent.REGISTRATION, "Pedido criado");

		// notifica o proprietário que o pedido foi recebido
		emailService.sendRequestStatusNotification(saved.getOwner().getEmail(), saved.getOwner().getFirstName(),
				saved.getPublicId().toString(), RequestStatus.PENDING);

		return requestMapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RequestResponse> findAll() {
		return requestRepository.findAll().stream().map(requestMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public RequestResponse findByPublicId(UUID publicId) {
		return requestRepository.findByPublicId(publicId).map(requestMapper::toResponse)
				.orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<RequestResponse> findByStatus(RequestStatus status) {
		return requestRepository.findByStatus(status).stream().map(requestMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<RequestResponse> findByOwner(UUID ownerPublicId) {
		Owner owner = ownerRepository.findByPublicId(ownerPublicId)
				.orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));

		return requestRepository.findByOwnerId(owner.getId()).stream().map(requestMapper::toResponse).toList();
	}

	@Override
	@Transactional
	public RequestResponse cancel(UUID publicId) {
		Request request = requestRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

		if (request.getStatus() == RequestStatus.ISSUED) {
			throw new RuntimeException("Não é possível cancelar um pedido já emitido");
		}

		request.setStatus(RequestStatus.CANCELLED);
		Request saved = requestRepository.save(request);

		// regista o evento de cancelamento no histórico
		historyService.record(saved.getPublicId(), HistoryEvent.CANCELLATION, "Pedido cancelado");

		// notifica o proprietário do cancelamento
		emailService.sendRequestStatusNotification(saved.getOwner().getEmail(), saved.getOwner().getFirstName(),
				saved.getPublicId().toString(), RequestStatus.CANCELLED);

		return requestMapper.toResponse(saved);
	}
}