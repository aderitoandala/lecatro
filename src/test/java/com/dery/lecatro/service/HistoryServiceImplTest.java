package com.dery.lecatro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dery.lecatro.dto.response.HistoryResponse;
import com.dery.lecatro.entity.History;
import com.dery.lecatro.entity.Request;
import com.dery.lecatro.entity.enums.HistoryEvent;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.HistoryMapper;
import com.dery.lecatro.repository.HistoryRepository;
import com.dery.lecatro.repository.RequestRepository;
import com.dery.lecatro.service.impl.HistoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

	@Mock
	private HistoryRepository historyRepository;

	@Mock
	private RequestRepository requestRepository;

	@Mock
	private HistoryMapper historyMapper;

	@InjectMocks
	private HistoryServiceImpl historyService;

	private Request request;
	private History history;
	private HistoryResponse historyResponse;
	private UUID requestPublicId;

	@BeforeEach
	void setUp() {
		requestPublicId = UUID.randomUUID();

		request = Request.builder().id(1L).publicId(requestPublicId).build();

		history = History.builder().id(1L).request(request).event(HistoryEvent.REGISTRATION)
				.description("Pedido criado").occurredAt(LocalDateTime.now()).build();

		historyResponse = new HistoryResponse(HistoryEvent.REGISTRATION, "Pedido criado", LocalDateTime.now());
	}

	@Test
	void shouldRecordHistoryEventSuccessfully() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));

		// Act
		historyService.record(requestPublicId, HistoryEvent.REGISTRATION, "Pedido criado");

		// Assert 
		verify(historyRepository, times(1)).save(any(History.class));
	}

	@Test
	void shouldThrowExceptionWhenRequestNotFoundOnRecord() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class,
				() -> historyService.record(requestPublicId, HistoryEvent.REGISTRATION, "Pedido criado"));
		verify(historyRepository, never()).save(any());
	}

	@Test
	void shouldListHistoryByRequestFromMostRecentToOldest() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.of(request));
		when(historyRepository.findByRequestIdOrderByOccurredAtDesc(1L)).thenReturn(List.of(history));
		when(historyMapper.toResponse(history)).thenReturn(historyResponse);

		// Act
		List<HistoryResponse> result = historyService.findByRequest(requestPublicId);

		// Assert 
		assertThat(result).hasSize(1);
		assertThat(result.get(0).event()).isEqualTo(HistoryEvent.REGISTRATION);
		verify(historyRepository, times(1)).findByRequestIdOrderByOccurredAtDesc(1L);
	}

	@Test
	void shouldThrowExceptionWhenRequestNotFoundOnList() {
		// Arrange
		when(requestRepository.findByPublicId(requestPublicId)).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(ResourceNotFoundException.class, ()-> historyService.findByRequest(requestPublicId));
		
	}
}