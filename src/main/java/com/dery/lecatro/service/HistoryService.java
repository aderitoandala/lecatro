package com.dery.lecatro.service;

import com.dery.lecatro.dto.response.HistoryResponse;
import com.dery.lecatro.entity.enums.HistoryEvent;

import java.util.List;
import java.util.UUID;

public interface HistoryService {

	void record(UUID requestPublicId, HistoryEvent event, String description);

	List<HistoryResponse> findByRequest(UUID requestPublicId);
}