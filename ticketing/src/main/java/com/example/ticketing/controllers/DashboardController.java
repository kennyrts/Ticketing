package com.example.ticketing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ticketing.models.Enfant;
import com.example.ticketing.repositories.EnfantRepository;

@Controller
public class DashboardController {
    
    @Autowired
    private EnfantRepository enfantRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("enfants", enfantRepository.findAll());
        return "dashboard";
    }
    
    @PostMapping("/enfant/add")
    public String addEnfant(@RequestParam("ageMax") Integer ageMax,
                           @RequestParam("reduction") Double reduction,
                           RedirectAttributes redirectAttributes) {
        try {
            Enfant enfant = new Enfant();
            enfant.setAgeMax(ageMax);
            enfant.setReduction(reduction);
            enfantRepository.save(enfant);
            redirectAttributes.addFlashAttribute("success", "Enfant ajouté avec succès!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
} 