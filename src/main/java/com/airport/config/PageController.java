package com.airport.config;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Главная страница с рейсами
    @GetMapping("/flights")
    public String flightsPage() {
        return "flights";  // Шаблон flights.html из resources/templates
    }

    // Страница входа
    @GetMapping("/login")
    public String loginPage() {
        return "login";  // Шаблон login.html из resources/templates
    }

    // Страница регистрации
    @GetMapping("/register")
    public String registerPage() {
        return "register";  // Шаблон register.html из resources/templates
    }

    // Страница статистики для менеджера
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/statistics")
    public String managerStatsPage() {
        return "manager-statistics";  // Шаблон manager-stats.html из resources/templates
    }

    @GetMapping("/tickets")
    public String ticketsPage() {
        return "tickets";  // Шаблон manager-stats.html из resources/templates
    }
}
