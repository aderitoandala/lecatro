package com.dery.lecatro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

	@GetMapping("/login")
	public String loginPage(@RequestParam(required = false) String erro, @RequestParam(required = false) String logout,
			Model model) {
		if (erro != null)
			model.addAttribute("erro", "Email ou senha incorrectos");
		if (logout != null)
			model.addAttribute("mensagem", "Sessão terminada com sucesso");
		return "auth/login";
	}
}