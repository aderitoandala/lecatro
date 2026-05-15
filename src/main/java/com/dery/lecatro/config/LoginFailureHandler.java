package com.dery.lecatro.config;

import com.dery.lecatro.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

	private final LoginAttemptService loginAttemptService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {

		String email = request.getParameter("email");

		if (exception instanceof LockedException) {
			// conta já bloqueada — não incrementa tentativas
			response.sendRedirect("/login?bloqueada");
		} else {
			// credenciais erradas — regista a tentativa falhada
			loginAttemptService.registerFailure(email);
			response.sendRedirect("/login?erro");
		}
	}
}