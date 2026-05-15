package com.dery.lecatro.service;

public interface LoginAttemptService {

	// regista uma tentativa falhada, incrementa contador e bloqueia se atingir o
	// limite
	void registerFailure(String email);

	// repõe o contador após login bem sucedido
	void registerSuccess(String email);
}