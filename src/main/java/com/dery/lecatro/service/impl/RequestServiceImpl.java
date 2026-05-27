package com.dery.lecatro.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public Page<RequestResponse> findAll(Pageable pageable) {

		return requestRepository.findAll(pageable).map(requestMapper::toResponse);
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

		boolean hasPendingRequest = requestRepository.existsByVehicleIdAndStatusIn(vehicle.getId(),
				List.of(RequestStatus.PENDING, RequestStatus.PAID));

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
	public Page<RequestResponse> findWithFilters(Integer year, Integer month, RequestStatus status, Pageable pageable) {
		Page<Request> requestsPage;

		Pageable sortedPageable = pageable.getSort().isSorted() ? pageable
				: PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));

		if (year != null && month != null) {
			requestsPage = requestRepository.findByYearAndMonth(year, month, sortedPageable);
		} else if (year != null) {
			requestsPage = requestRepository.findByYear(year, sortedPageable);
		} else {
			requestsPage = requestRepository.findAll(sortedPageable);
		}

		return requestsPage.map(requestMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public RequestStatsResponse getStatsByYear(int year) {

		List<Request> requests = requestRepository.findByYear(year);

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
	public Page<RequestResponse> findByStatus(RequestStatus status, Pageable pageable) {
		return requestRepository.findByStatus(status, pageable).map(requestMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RequestResponse> findByOwner(UUID ownerPublicId, Pageable pageable) {
		Owner owner = ownerRepository.findByPublicId(ownerPublicId)
				.orElseThrow(() -> new ResourceNotFoundException("Proprietário não encontrado"));

		return requestRepository.findByOwnerId(owner.getId(), pageable).map(requestMapper::toResponse);
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

		historyService.record(saved.getPublicId(), HistoryEvent.CANCELLATION, "Pedido cancelado");

		emailService.sendRequestStatusNotification(saved.getOwner().getEmail(), saved.getOwner().getFirstName(),
				saved.getPublicId().toString(), RequestStatus.CANCELLED);

		return requestMapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RequestResponse> findToday(Pageable pageable) {
		LocalDate today = LocalDate.now();
		LocalDateTime start = today.atStartOfDay();
		LocalDateTime end = today.atTime(LocalTime.MAX);

		return requestRepository.findByCreatedAtBetween(start, end, pageable).map(requestMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RequestResponse> findAwaitingAction(Pageable pageable) {
		List<RequestStatus> activeStatuses = List.of(RequestStatus.PENDING, RequestStatus.PAID);

		return requestRepository.findByStatusIn(activeStatuses, pageable).map(requestMapper::toResponse);
	}
}