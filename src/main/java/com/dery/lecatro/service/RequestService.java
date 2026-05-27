package com.dery.lecatro.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dery.lecatro.dto.request.RequestRequest;
import com.dery.lecatro.dto.response.RequestResponse;
import com.dery.lecatro.dto.response.RequestStatsResponse;
import com.dery.lecatro.entity.enums.RequestStatus;

public interface RequestService {

	RequestResponse create(RequestRequest request);

	RequestResponse findByPublicId(UUID publicId);

	RequestResponse cancel(UUID publicId);

	RequestStatsResponse getStatsByYear(int year);

	Page<RequestResponse> findByStatus(RequestStatus status, Pageable pageable);

	Page<RequestResponse> findByOwner(UUID ownerPublicId, Pageable pageable);

	Page<RequestResponse> findWithFilters(Integer year, Integer month, RequestStatus status, Pageable pageable);

	Page<RequestResponse> findToday(Pageable pageable);

	Page<RequestResponse> findAll(Pageable pageable);

	Page<RequestResponse> findAwaitingAction(Pageable pageable);
}