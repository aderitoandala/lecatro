package com.dery.lecatro.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dery.lecatro.entity.enums.LicensePlateStatus;
import com.dery.lecatro.exception.ResourceNotFoundException;
import com.dery.lecatro.service.LicensePlateService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/license-plates")
@RequiredArgsConstructor
public class LicensePlateController {

	private final LicensePlateService licensePlateService;

	@PostMapping("/issue/{requestPublicId}")
	public String issue(@PathVariable UUID requestPublicId, RedirectAttributes redirectAttributes) {

		licensePlateService.issue(requestPublicId);
		redirectAttributes.addFlashAttribute("mensagem", "Matrícula emitida com sucesso");
		return "redirect:/requests";
	}

	@GetMapping("/search")
	public String searchForm(Model model) {

		model.addAttribute("plates", licensePlateService.findByStatus(LicensePlateStatus.ACTIVE));

		return "license-plate/search";
	}

	@GetMapping("/search/result")
	public String searchResult(@RequestParam String number, Model model) {
		try {
			model.addAttribute("licensePlate", licensePlateService.findByNumber(number));
		} catch (ResourceNotFoundException e) {

			model.addAttribute("licensePlate", null);
		}
		return "license-plate/search";
	}

	@PostMapping("/{publicId}/cancel")
	public String cancel(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
		licensePlateService.cancel(publicId);
		redirectAttributes.addFlashAttribute("mensagem", "Matrícula cancelada com sucesso");
		return "redirect:/requests";
	}
}