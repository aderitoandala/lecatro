package com.dery.lecatro.controller;

import com.dery.lecatro.dto.request.UserRequest;
import com.dery.lecatro.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

	private final UserService userService;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("users", userService.findAll());
		return "user/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("userRequest", new UserRequest(null, null, null, null));
		return "user/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute UserRequest userRequest, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors())
			return "user/form";

		userService.create(userRequest);
		redirectAttributes.addFlashAttribute("mensagem", "Utilizador criado com sucesso");
		return "redirect:/users";
	}

	@GetMapping("/{publicId}/edit")
	public String editForm(@PathVariable UUID publicId, Model model) {
		model.addAttribute("user", userService.findByPublicId(publicId));
		model.addAttribute("publicId", publicId);
		return "user/form";
	}

	@PostMapping("/{publicId}/edit")
	public String update(@PathVariable UUID publicId, @Valid @ModelAttribute UserRequest userRequest,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors())
			return "user/form";

		userService.update(publicId, userRequest);
		redirectAttributes.addFlashAttribute("mensagem", "Utilizador actualizado com sucesso");
		return "redirect:/users";
	}

	@PostMapping("/{publicId}/delete")
	public String delete(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
		userService.delete(publicId);
		redirectAttributes.addFlashAttribute("mensagem", "Utilizador removido com sucesso");
		return "redirect:/users";
	}
}