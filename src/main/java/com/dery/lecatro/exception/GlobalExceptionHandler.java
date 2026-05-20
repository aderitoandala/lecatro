package com.dery.lecatro.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public String handleResourceNotFound(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {
		log.warn("Resource not found: {}", ex.getMessage());
		redirectAttributes.addFlashAttribute("erro", ex.getMessage());
		return "redirect:/dashboard";
	}

	@ExceptionHandler(BusinessException.class)
	public String handleBusiness(BusinessException ex, RedirectAttributes redirectAttributes) {
		log.warn("Business rule violation: {}", ex.getMessage());
		redirectAttributes.addFlashAttribute("erro", ex.getMessage());
		return "redirect:/dashboard";
	}

	@ExceptionHandler(DataIntegrityException.class)
	public String handleDataIntegrity(DataIntegrityException ex, RedirectAttributes redirectAttributes) {
		log.warn("Data integrity: {}", ex.getMessage());
		redirectAttributes.addFlashAttribute("erro", ex.getMessage());
		return "redirect:/dashboard";
	}

	@ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
	public String handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex,
			RedirectAttributes redirectAttributes) {
		log.warn("referential integrity violation: {}", ex.getMessage());

		redirectAttributes.addFlashAttribute("erro",
				"Não é possível realizar esta operação porque o registo está associado a outros dados no sistema.");
		return "redirect:/dashboard";
	}

	@ExceptionHandler(Exception.class)
	public String handleGeneric(Exception ex, Model model) {
		if (ex instanceof org.springframework.web.servlet.resource.NoResourceFoundException) {
			return "redirect:/dashboard";
		}
		log.error("Unexpected error: {}", ex.getMessage(), ex);
		model.addAttribute("erro", "Ocorreu um erro inesperado. Tente novamente.");
		return "error";
	}
}