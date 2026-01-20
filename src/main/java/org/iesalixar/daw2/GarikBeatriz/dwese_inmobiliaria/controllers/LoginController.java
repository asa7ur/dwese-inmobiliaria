package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("activePage", "login");
        return "login";
    }
}