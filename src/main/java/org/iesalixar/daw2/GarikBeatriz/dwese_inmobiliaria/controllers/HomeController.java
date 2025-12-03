package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository; // O tu Service si lo usas
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    // 1. Inyectamos el repositorio para poder buscar datos
    @Autowired
    private PropertyRepository propertyRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("activePage", "home");
        Pageable limit = PageRequest.of(0, 3, Sort.by("id").ascending());
        List<Property> properties = propertyRepository.findAll(limit).getContent();
        model.addAttribute("featuredProperties", properties);
        return "index";
    }
}