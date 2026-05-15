package com.dery.lecatro.service;

import java.util.List;
import java.util.UUID;

import com.dery.lecatro.dto.request.RequestRequest;
import com.dery.lecatro.dto.response.RequestResponse;
import com.dery.lecatro.dto.response.RequestStatsResponse;
import com.dery.lecatro.entity.enums.RequestStatus;

public interface RequestService {

	RequestResponse create(RequestRequest request);

	List<RequestResponse> findAll();

	RequestResponse findByPublicId(UUID publicId);

	List<RequestResponse> findByStatus(RequestStatus status);

	List<RequestResponse> findByOwner(UUID ownerPublicId);

	RequestResponse cancel(UUID publicId);

	List<RequestResponse> findWithFilters(Integer year, Integer month, RequestStatus status);

	RequestStatsResponse getStatsByYear(int year);
}