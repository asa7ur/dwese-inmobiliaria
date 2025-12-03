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
        // Marcamos la página activa para el menú
        model.addAttribute("activePage", "home");

        // 2. Lógica para obtener las "Propiedades Recientes"
        // Creamos una petición de página: Página 0, Tamaño 3, Ordenado por ID descendente (lo último creado primero)
        // Si quieres mostrar 6 casas, cambia el '3' por un '6'.
        Pageable limit = PageRequest.of(0, 3, Sort.by("id").descending());

        // Obtenemos la lista. .getContent() extrae la lista de la página
        List<Property> properties = propertyRepository.findAll(limit).getContent();

        // 3. Pasamos la lista a la vista "index.html"
        // IMPORTANTE: El nombre "featuredProperties" debe coincidir con el th:each del HTML
        model.addAttribute("featuredProperties", properties);

        return "index";
    }
}