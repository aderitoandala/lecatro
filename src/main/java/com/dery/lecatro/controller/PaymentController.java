package com.dery.lecatro.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dery.lecatro.dto.request.PaymentRequest;
import com.dery.lecatro.service.PaymentService;
import com.dery.lecatro.service.RequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	private final RequestService requestService;

	@GetMapping("/new/{requestPublicId}")
	public String createForm(@PathVariable UUID requestPublicId, Model model) {

		model.addAttribute("request", requestService.findByPublicId(requestPublicId));
		model.addAttribute("paymentRequest", new PaymentRequest(requestPublicId, null, null));
		return "payment/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute PaymentRequest paymentRequest, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors())
			return "payment/form";

		paymentService.create(paymentRequest);
		redirectAttributes.addFlashAttribute("mensagem", "Pagamento registado com sucesso");
		return "redirect:/requests";
	}

	@PostMapping("/{publicId}/confirm")
	public String confirm(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
		paymentService.confirm(publicId);
		redirectAttributes.addFlashAttribute("mensagem", "Pagamento confirmado com sucesso");
		return "redirect:/requests";
	}

	@PostMapping("/{publicId}/reject")
	public String reject(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
		paymentService.reject(publicId);
		redirectAttributes.addFlashAttribute("mensagem", "Pagamento rejeitado");
		return "redirect:/requests";
	}
}