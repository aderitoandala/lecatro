package com.dery.lecatro.service;

import com.dery.lecatro.dto.request.UserRequest;
import com.dery.lecatro.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

	UserResponse create(UserRequest request);

	List<UserResponse> findAll();

	UserResponse findByPublicId(UUID publicId);

	UserResponse update(UUID publicId, UserRequest request);

	void delete(UUID publicId);
}