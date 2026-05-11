package com.dery.lecatro.service;

import com.dery.lecatro.dto.request.VehicleRequest;
import com.dery.lecatro.dto.response.VehicleResponse;

import java.util.List;
import java.util.UUID;

public interface VehicleService {

	VehicleResponse create(VehicleRequest request);

	List<VehicleResponse> findAll();

	VehicleResponse findByPublicId(UUID publicId);

	VehicleResponse update(UUID publicId, VehicleRequest request);

	void delete(UUID publicId);
}