package com.dery.lecatro.service.impl;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dery.lecatro.dto.response.LicensePlateResponse;
import com.dery.lecatro.entity.LicensePlate;
import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.enums.HistoryEvent;
import com.dery.lecatro.entity.enums.LicensePlateStatus;
import com.dery.lecatro.entity.enums.RequestStatus;
import com.dery.lecatro.exception.BusinessException;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.LicensePlateMapper;
import com.dery.lecatro.repository.LicensePlateRepository;
import com.dery.lecatro.repository.RequestRepository;
import com.dery.lecatro.service.EmailService;
import com.dery.lecatro.service.HistoryService;
import com.dery.lecatro.service.LicensePlateService;
import com.dery.lecatro.util.LicensePlateGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LicensePlateServiceImpl implements LicensePlateService {

	private final LicensePlateRepository licensePlateRepository;
	private final RequestRepository requestRepository;
	private final LicensePlateMapper licensePlateMapper;
	private final LicensePlateGenerator licensePlateGenerator; 
	private final HistoryService historyService;
	private final EmailService emailService;

	@Override
	@Transactional
	public LicensePlateResponse issue(UUID requestPublicId) {
		Request request = requestRepository.findByPublicId(requestPublicId)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

		// A matricula so e emitida apos a confirmacao do pagamento
		if (request.getStatus() != RequestStatus.PAID) {
			throw new BusinessException("O pagamento ainda não foi confirmado");
		}

		// O veículo não pode ter matrícula activa
		if (licensePlateRepository.existsByRequestVehicleIdAndStatus(request.getVehicle().getId(),
				LicensePlateStatus.ACTIVE)) {
			throw new BusinessException("Este veículo já tem uma matrícula activa");
		}

		// gera o número da matrícula
		String number = licensePlateGenerator.generate(request.getUser().getProvince());

		//cria a matricula
		LicensePlate licensePlate = LicensePlate.builder().request(request).number(number).issueDate(LocalDate.now())
				.status(LicensePlateStatus.ACTIVE).build();

		// actualiza o estado do pedido para ISSUED(matricula emitida)
		request.setStatus(RequestStatus.ISSUED);
		requestRepository.save(request);

		LicensePlate saved = licensePlateRepository.save(licensePlate);

		// regista emissão no histórico
		historyService.record(request.getPublicId(), HistoryEvent.REGISTRATION, "Matrícula emitida: " + number);
		
		// notifica o proprietário que a matrícula foi emitida
		emailService.sendRequestStatusNotification(
		    request.getOwner().getEmail(),
		    request.getOwner().getFirstName(),
		    request.getPublicId().toString(),
		    RequestStatus.ISSUED
		);

		return licensePlateMapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public LicensePlateResponse findByNumber(String number) {
		return licensePlateRepository.findByNumber(number).map(licensePlateMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));
	}

	@Override
	@Transactional
	public LicensePlateResponse cancel(UUID publicId) {
		LicensePlate licensePlate = licensePlateRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));

		licensePlate.setStatus(LicensePlateStatus.CANCELLED);
		LicensePlate saved = licensePlateRepository.save(licensePlate);

		// regista cancelamento no histórico
		historyService.record(saved.getRequest().getPublicId(), HistoryEvent.CANCELLATION,
				"Matrícula cancelada: " + saved.getNumber());

		return licensePlateMapper.toResponse(saved);
	}
}