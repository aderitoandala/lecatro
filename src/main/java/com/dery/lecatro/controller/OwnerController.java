package com.dery.lecatro.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
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

import com.dery.lecatro.dto.request.OwnerRequest;
import com.dery.lecatro.dto.response.OwnerResponse;
import com.dery.lecatro.exception.DataIntegrityException;
import com.dery.lecatro.service.OwnerService;
import com.dery.lecatro.util.PdfGenerator;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {

	private final OwnerService ownerService;
	private final PdfGenerator pdfGenerator;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("owners", ownerService.findAll());
		return "owner/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("form", new OwnerRequest(null, null, null, null, null));
		return "owner/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute OwnerRequest form, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors())
			return "owner/form";
		ownerService.create(form);
		redirectAttributes.addFlashAttribute("mensagem", "Proprietário criado com sucesso");
		return "redirect:/owners";
	}

	@GetMapping("/{publicId}/edit")
	public String editForm(@PathVariable UUID publicId, Model model) {
		OwnerResponse owner = ownerService.findByPublicId(publicId);
		model.addAttribute("form",
				new OwnerRequest(owner.firstName(), owner.lastName(), owner.nuit(), owner.email(), owner.birthDate()));
		model.addAttribute("publicId", publicId);
		model.addAttribute("editMode", true);
		return "owner/form";
	}

	@PostMapping("/{publicId}/edit")
	public String update(@PathVariable UUID publicId, @Valid @ModelAttribute OwnerRequest form, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors())
			return "owner/form";

		try {
			ownerService.update(publicId, form);
			redirectAttributes.addFlashAttribute("mensagem", "Proprietário actualizado com sucesso");
			return "redirect:/owners";
		} catch (DataIntegrityException e) {

			redirectAttributes.addFlashAttribute("erro", e.getMessage());
			return "redirect:/owners/" + publicId + "/edit";
		}
	}

	@PostMapping("/{publicId}/delete")
	public String delete(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
		try {
			ownerService.delete(publicId);
			redirectAttributes.addFlashAttribute("mensagem", "Proprietário removido com sucesso");
		} catch (org.springframework.dao.DataIntegrityViolationException e) {

			redirectAttributes.addFlashAttribute("erro",
					"Este proprietário não pode ser eliminado porque tem pedidos associados.");
		}
		return "redirect:/owners";
	}

	@GetMapping("/pdf")
	public void exportPdf(HttpServletResponse response) throws Exception {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=proprietarios.pdf");

		List<String[]> rows = ownerService.findAll().stream()
				.map(o -> new String[] { o.firstName() + " " + o.lastName(), o.nuit(), o.email(),
						o.birthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) })
				.toList();

		pdfGenerator.generateOwners(rows, response.getOutputStream());
	}
}