package com.dery.lecatro.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dery.lecatro.dto.request.UserRequest;
import com.dery.lecatro.dto.response.UserResponse;
import com.dery.lecatro.entity.User;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.mapper.UserMapper;
import com.dery.lecatro.repository.UserRepository;
import com.dery.lecatro.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserResponse create(UserRequest request) {

		User user = userMapper.toEntity(request);

		user.setPassword(passwordEncoder.encode(request.password()));

		return userMapper.toResponse(userRepository.save(user));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> findAll() {
		return userRepository.findAll().stream().map(userMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse findByPublicId(UUID publicId) {
		return userRepository.findByPublicId(publicId).map(userMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));
	}

	@Override
	@Transactional
	public UserResponse update(UUID publicId, UserRequest request) {

		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));

		user.setEmail(request.email());
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setProvince(request.province());
		user.setRole(request.role());

		return userMapper.toResponse(userRepository.save(user));
	}

	@Override
	@Transactional
	public void delete(UUID publicId) {
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));

		userRepository.delete(user);
	}
}