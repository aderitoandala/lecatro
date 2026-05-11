package com.dery.lecatro.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.repository.LicensePlateRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LicensePlateGenerator {

	private final LicensePlateRepository licensePlateRepository;

	private static final SecureRandom RANDOM = new SecureRandom();

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static final String DIGITS = "0123456789";

	public String generate(Province province) {
		String number;

		// gera até encontrar um número único no sistema
		do {
			number = buildNumber(province);
		} while (licensePlateRepository.existsByNumber(number));

		return number;
	}

	private String buildNumber(Province province) {
		// pega três letras aleatórias
		String letters = randomChars(LETTERS, 3);

		// pega três dígitos aleatórios
		String digits = randomChars(DIGITS, 3);

		// pega o código da provincia(Ex:MC)
		String provinceCode = province.getCode();

		// formata a matricula (Ex:ABC 123 MC)
		return letters + " " + digits + " " + provinceCode;
	}

	private String randomChars(String source, int count) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < count; i++) {
			// SecureRandom garante distribuição imprevisível
			sb.append(source.charAt(RANDOM.nextInt(source.length())));
		}

		return sb.toString();
	}
}