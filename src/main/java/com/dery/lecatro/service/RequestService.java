package com.dery.lecatro.service;

import com.dery.lecatro.dto.request.RequestRequest;
import com.dery.lecatro.dto.response.RequestResponse;
import com.dery.lecatro.entity.enums.RequestStatus;

import java.util.List;
import java.util.UUID;

public interface RequestService {

	RequestResponse create(RequestRequest request);

	List<RequestResponse> findAll();

	RequestResponse findByPublicId(UUID publicId);

	List<RequestResponse> findByStatus(RequestStatus status);

	List<RequestResponse> findByOwner(UUID ownerPublicId);

	RequestResponse cancel(UUID publicId);
}