package com.dery.lecatro.service;

import java.util.List;
import java.util.UUID;

import com.dery.lecatro.dto.request.UserRequest;
import com.dery.lecatro.dto.request.UserUpdateRequest;
import com.dery.lecatro.dto.response.UserResponse;
import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.entity.enums.Role;

public interface UserService {

	UserResponse create(UserRequest request);

	List<UserResponse> findAll();

	UserResponse findByPublicId(UUID publicId);

	UserResponse update(UUID publicId, UserUpdateRequest request);

	void delete(UUID publicId);

	List<UserResponse> findBySearch(String search);
	
	List<UserResponse> findWithFilters(String search, Role role, Province province);
}