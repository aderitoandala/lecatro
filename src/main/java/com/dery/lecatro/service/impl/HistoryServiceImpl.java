package com.dery.lecatro.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dery.lecatro.dto.response.HistoryResponse;
import com.dery.lecatro.entity.History;
import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.enums.HistoryEvent;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.HistoryMapper;
import com.dery.lecatro.repository.HistoryRepository;
import com.dery.lecatro.repository.RequestRepository;
import com.dery.lecatro.service.HistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

	private final HistoryRepository historyRepository;
	private final RequestRepository requestRepository;
	private final HistoryMapper historyMapper;

	@Override
	@Transactional
	public void record(UUID requestPublicId, HistoryEvent event, String description) {
		Request request = requestRepository.findByPublicId(requestPublicId)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

		
		History history = History.builder().request(request).event(event).description(description).build();

		historyRepository.save(history);
	}

	@Override
	@Transactional(readOnly = true)
	public List<HistoryResponse> findByRequest(UUID requestPublicId) {
		Request request = requestRepository.findByPublicId(requestPublicId)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

		// exibe o historico de um pedido a partir do evento mais recente
		return historyRepository.findByRequestIdOrderByOccurredAtDesc(request.getId()).stream()
				.map(historyMapper::toResponse).toList();
	}
}