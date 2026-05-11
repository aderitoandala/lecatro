package com.dery.lecatro.controller;

import com.dery.lecatro.dto.request.OwnerRequest;
import com.dery.lecatro.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("owners", ownerService.findAll());
        return "owner/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("ownerRequest", new OwnerRequest(null, null, null, null, null));
        return "owner/form";
    }

    @PostMapping
    public String create(
        @Valid @ModelAttribute OwnerRequest ownerRequest,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) return "owner/form";

        ownerService.create(ownerRequest);
        redirectAttributes.addFlashAttribute("mensagem", "Proprietário criado com sucesso");
        return "redirect:/owners";
    }

    @GetMapping("/{publicId}/edit")
    public String editForm(@PathVariable UUID publicId, Model model) {
        model.addAttribute("owner", ownerService.findByPublicId(publicId));
        model.addAttribute("publicId", publicId);
        return "owner/form";
    }

    @PostMapping("/{publicId}/edit")
    public String update(
        @PathVariable UUID publicId,
        @Valid @ModelAttribute OwnerRequest ownerRequest,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) return "owner/form";

        ownerService.update(publicId, ownerRequest);
        redirectAttributes.addFlashAttribute("mensagem", "Proprietário actualizado com sucesso");
        return "redirect:/owners";
    }

    @PostMapping("/{publicId}/delete")
    public String delete(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
        ownerService.delete(publicId);
        redirectAttributes.addFlashAttribute("mensagem", "Proprietário removido com sucesso");
        return "redirect:/owners";
    }
}