package com.dery.lecatro.controller;

import com.dery.lecatro.service.LicensePlateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

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
    public String searchForm() {
        return "license-plate/search";
    }

    @GetMapping("/search/result")
    public String searchResult(@RequestParam String number, Model model) {
        model.addAttribute("licensePlate", licensePlateService.findByNumber(number));
        return "license-plate/search";
    }

    @PostMapping("/{publicId}/cancel")
    public String cancel(@PathVariable UUID publicId, RedirectAttributes redirectAttributes) {
        licensePlateService.cancel(publicId);
        redirectAttributes.addFlashAttribute("mensagem", "Matrícula cancelada com sucesso");
        return "redirect:/requests";
    }
}