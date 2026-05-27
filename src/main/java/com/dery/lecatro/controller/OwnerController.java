package com.dery.lecatro.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	public String list(@RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Model model) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());

		Page<OwnerResponse> pageResult = (search != null && !search.isBlank())
				? ownerService.findBySearch(search, pageable)
				: ownerService.findAll(pageable);

		model.addAttribute("owners", pageResult.getContent());
		model.addAttribute("currentPage", pageResult.getNumber());
		model.addAttribute("totalPages", pageResult.getTotalPages());
		model.addAttribute("totalItems", pageResult.getTotalElements());
		model.addAttribute("pageSize", size);
		model.addAttribute("search", search);
		model.addAttribute("queryString", buildQueryString(search, size));
		return "owner/list";
	}

	private String buildQueryString(String search, int size) {
		StringBuilder sb = new StringBuilder("?size=" + size);
		if (search != null && !search.isBlank())
			sb.append("&search=").append(search);
		return sb.toString();
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("form", new OwnerRequest(null, null, null, null, null));
		return "owner/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute("form") OwnerRequest form, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {

			return "owner/form";
		}

		try {

			ownerService.create(form);
			redirectAttributes.addFlashAttribute("mensagem", "Proprietário criado com sucesso");
			return "redirect:/owners";

		} catch (DataIntegrityException e) {

			model.addAttribute("erro", e.getMessage());

			return "owner/form";
		}
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
					"Este proprietário não pode ser removido porque tem pedidos associados.");
		}
		return "redirect:/owners";
	}

	@GetMapping("/pdf")
	public void exportPdf(HttpServletResponse response) throws Exception {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=proprietarios.pdf");

		List<String[]> rows = ownerService.findAll(Pageable.unpaged()).getContent().stream()
				.map(o -> new String[] { o.firstName() + " " + o.lastName(), o.nuit(), o.email(),
						o.birthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) })
				.toList();

		pdfGenerator.generateOwners(rows, response.getOutputStream());
	}

}