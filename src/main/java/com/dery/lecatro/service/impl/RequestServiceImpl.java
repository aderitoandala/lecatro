package com.dery.lecatro.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dery.lecatro.dto.request.RequestRequest;
import com.dery.lecatro.dto.response.RequestResponse;
import com.dery.lecatro.dto.response.RequestStatsResponse;
import com.dery.lecatro.entity.Owner;
import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.User;
import com.dery.lecatro.entity.Vehicle;
import com.dery.lecatro.entity.enums.HistoryEvent;
import com.dery.lecatro.entity.enums.LicensePlateStatus;
import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.exception.BusinessException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.RequestMapper;
import com.dery.lecatro.repository.LicensePlateRepository;
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
	private final LicensePlateRepository licensePlateRepository;
	private final RequestMapper requestMapper;
	private final HistoryService historyService;
	private final EmailService emailService;

	@Override
	@Transactional(readOnly = true)
	public List<RequestResponse> findAll() {
		return requestRepository.findAll().stream().map(requestMapper::toResponse).toList();
	}

	@Override
	@Transactional
	public RequestResponse create(RequestRequest request) {

		Owner owner = ownerRepository.findByPublicId(request.ownerPublicId())
				.orElseThrow(() -> new ResourceNotFoundException("Proprietário não encontrado"));

		Vehicle vehicle = vehicleRepository.findByPublicId(request.vehiclePublicId())
				.orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));

		// verifica se o veículo já tem matrícula activa
		if (licensePlateRepository.existsByRequestVehicleIdAndStatus(vehicle.getId(), LicensePlateStatus.ACTIVE)) {
			throw new BusinessException("O veículo " + vehicle.getBrand() + " " + vehicle.getModel() + " (chassis: "
					+ vehicle.getChassisNumber() + ")" + " já possui uma matrícula activa. "
					+ "Cancele a matrícula existente antes de criar um novo pedido.");
		}

		// verifica se já existe pedido PENDING ou PAID para este veículo
		boolean hasPendingRequest = requestRepository.findByVehicleId(vehicle.getId()).stream()
				.anyMatch(r -> r.getStatus() == RequestStatus.PENDING || r.getStatus() == RequestStatus.PAID);

		if (hasPendingRequest) {
			throw new BusinessException("O veículo " + vehicle.getBrand() + " " + vehicle.getModel()
					+ " já possui um processo em andamento. "
					+ "Conclua ou cancele o pedido existente antes de criar um novo.");
		}

		// obtém o utilizador autenticado que está a processar o pedido
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Utilizador autenticado não encontrado"));

		Request newRequest = Request.builder().owner(owner).vehicle(vehicle).user(user).build();

		Request saved = requestRepository.save(newRequest);

		historyService.record(saved.getPublicId(), HistoryEvent.REGISTRATION, "Pedido criado");

		emailService.sendRequestStatusNotification(saved.getOwner().getEmail(), saved.getOwner().getFirstName(),
				saved.getPublicId().toString(), RequestStatus.PENDING);

		return requestMapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RequestResponse> findWithFilters(Integer year, Integer month, RequestStatus status) {
		List<Request> requests;

		// aplica os filtros disponíveis — todos são opcionais
		if (year != null && month != null) {
			requests = requestRepository.findByYearAndMonth(year, month);
		} else if (year != null) {
			requests = requestRepository.findByYear(year);
		} else {
			requests = requestRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		}

		// filtra por estado se fornecido
		return requests.stream().filter(r -> status == null || r.getStatus() == status).map(requestMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public RequestStatsResponse getStatsByYear(int year) {
		List<Request> requests = requestRepository.findByYear(year);

		// agrega as contagens por estado
		return new RequestStatsResponse(year, null, requests.size(),
				requests.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count(),
				requests.stream().filter(r -> r.getStatus() == RequestStatus.PAID).count(),
				requests.stream().filter(r -> r.getStatus() == RequestStatus.ISSUED).count(),
				requests.stream().filter(r -> r.getStatus() == RequestStatus.CANCELLED).count());
	}

	@Override
	@Transactional(readOnly = true)
	public RequestResponse findByPublicId(UUID publicId) {
		return requestRepository.findByPublicId(publicId).map(requestMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
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
				.orElseThrow(() -> new ResourceNotFoundException("Proprietário não encontrado"));

		return requestRepository.findByOwnerId(owner.getId()).stream().map(requestMapper::toResponse).toList();
	}

	@Override
	@Transactional
	public RequestResponse cancel(UUID publicId) {
		Request request = requestRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

		if (request.getStatus() == RequestStatus.ISSUED) {
			throw new BusinessException("Não é possível cancelar um pedido já emitido");
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

	@Override
	@Transactional(readOnly = true)
	public List<RequestResponse> findToday() {
		LocalDate today = LocalDate.now();
		// Define o início do dia (00:00:00) e o fim do dia (23:59:59)
		LocalDateTime start = today.atStartOfDay();
		LocalDateTime end = today.atTime(LocalTime.MAX);

		return requestRepository.findByCreatedAtBetween(start, end).stream().map(requestMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<RequestResponse> findAwaitingAction() {

		List<RequestStatus> activeStatuses = List.of(RequestStatus.PENDING, RequestStatus.PAID);

		return requestRepository.findByStatusIn(activeStatuses).stream().map(requestMapper::toResponse).toList();
	}

}