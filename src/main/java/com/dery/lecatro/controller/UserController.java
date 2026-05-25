package com.dery.lecatro.controller;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dery.lecatro.dto.request.UserRequest;
import com.dery.lecatro.dto.request.UserUpdateRequest;
import com.dery.lecatro.dto.response.UserResponse;
import com.dery.lecatro.entity.enums.Province;
import com.dery.lecatro.entity.enums.Role;
import com.dery.lecatro.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

	private final UserService userService;

	@GetMapping
	public String list(@RequestParam(required = false) String search, @RequestParam(required = false) Role role,
			@RequestParam(required = false) Province province, Model model) {
		model.addAttribute("users", userService.findWithFilters(search, role, province));
		model.addAttribute("search", search);
		model.addAttribute("selectedRole", role);
		model.addAttribute("selectedProvince", province);
		model.addAttribute("roles", Role.values());
		model.addAttribute("provinces", Province.values());
		return "user/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("form", new UserRequest(null, null, null, null));
		return "user/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute("form") UserRequest form, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors())
			return "user/form";

		userService.create(form);
		redirectAttributes.addFlashAttribute("mensagem", "Utilizador criado com sucesso");
		return "redirect:/users";
	}

	@GetMapping("/{publicId}/edit")
	public String editForm(@PathVariable UUID publicId, Model model) {
		UserResponse user = userService.findByPublicId(publicId);
		model.addAttribute("form", new UserUpdateRequest(user.email(), null, user.province(), user.role()));
		model.addAttribute("publicId", publicId);
		model.addAttribute("editMode", true);
		return "user/form";
	}

	@PostMapping("/{publicId}/edit")
	public String update(@PathVariable UUID publicId, @Valid @ModelAttribute UserUpdateRequest form,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors())
			return "user/form";

		userService.update(publicId, form);
		redirectAttributes.addFlashAttribute("mensagem", "Utilizador actualizado com sucesso");
		return "redirect:/users";
	}

	@PostMapping("/{publicId}/delete")
	public String delete(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
		try {
			userService.delete(publicId);
			redirectAttributes.addFlashAttribute("mensagem", "Utilizador removido com sucesso");
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("erro",
					"Este utilizador não pode ser removido porque tem pedidos associados.");
		}
		return "redirect:/users";
	}
}