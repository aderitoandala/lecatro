package com.dery.lecatro.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dery.lecatro.repository.UserRepository;
import com.dery.lecatro.service.LoginAttemptService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptServiceImpl implements LoginAttemptService {

	private static final int MAX_ATTEMPTS = 3; // número máximo de tentativas
	private static final int LOCK_MINUTES = 15; // duração do bloqueio em minutos

	private final UserRepository userRepository;

	@Override
	@Transactional
	public void registerFailure(String email) {
		userRepository.findByEmail(email).ifPresent(user -> {

			int attempts = user.getFailedAttempts() + 1;
			user.setFailedAttempts(attempts);

			if (attempts >= MAX_ATTEMPTS) {
				// bloqueia a conta por LOCK_MINUTES (15 minutos)
				user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
				
				log.warn("Conta bloqueada por {} minutos — email: {}", LOCK_MINUTES, email);
			}

			userRepository.save(user);
		});
	}

	@Override
	@Transactional
	public void registerSuccess(String email) {
		userRepository.findByEmail(email).ifPresent(user -> {
			// repõe o contador e remove o bloqueio após login bem sucedido
			user.setFailedAttempts(0);
			user.setLockedUntil(null);
			userRepository.save(user);
		});
	}
}