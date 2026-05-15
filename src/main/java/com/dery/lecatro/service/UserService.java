package com.dery.lecatro.service;

import java.util.List;
import java.util.UUID;

import com.dery.lecatro.dto.request.UserRequest;
import com.dery.lecatro.dto.request.UserUpdateRequest;
import com.dery.lecatro.dto.response.UserResponse;

public interface UserService {

	UserResponse create(UserRequest request);

	List<UserResponse> findAll();

	UserResponse findByPublicId(UUID publicId);

	UserResponse update(UUID publicId, UserUpdateRequest request);

	void delete(UUID publicId);
}