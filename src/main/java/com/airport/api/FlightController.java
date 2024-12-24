package com.airport.api;

import com.airport.domain.Flight;
import com.airport.domain.Order;
import com.airport.domain.User;
import com.airport.persistence.FlightRepository;
import com.airport.persistence.OrderRepository;
import com.airport.persistence.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightRepository flightRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public FlightController(FlightRepository flightRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.flightRepository = flightRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // Доступ для просмотра всех рейсов (USER, ADMIN, MANAGER)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public Map<String, Object> getAllFlights(
            @RequestParam(required = false) String destination,
            @AuthenticationPrincipal User user
    ) {
        List<Flight> flights = (destination != null && !destination.isEmpty())
                ? flightRepository.findByDestinationContainingIgnoreCase(destination)
                : flightRepository.findAll();

        // Возвращаем список рейсов и роль пользователя
        return Map.of(
                "flights", flights,
                "userRole", user.getRole() // Получаем роль пользователя
        );
    }


    // Создание рейса — доступ только для администратора
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Flight createFlight(@RequestBody Flight flight) {
        return flightRepository.save(flight);
    }

    // Удаление рейса — доступ только для администратора
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFlight(@PathVariable Long id) {
        flightRepository.deleteById(id);
    }

    // Доступ для просмотра доступных рейсов (USER, ADMIN, MANAGER)
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public List<Flight> getAvailableFlights() {
        return flightRepository.findByIsAvailable(true);
    }

    // Покупка билета на рейс (USER, ADMIN, MANAGER)
    @PostMapping("/purchase")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Order purchaseTicket(@CookieValue("user_id") Long userId, @RequestBody Map<String, Object> body) {
        // Извлекаем flightId из тела запроса
        Long flightId = Long.parseLong(body.get("flightId").toString());

        // Находим пользователя и рейс
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found: " + flightId));

        // Проверка доступности рейса
        if (!flight.isAvailable()) {
            throw new IllegalStateException("Flight is not available for purchase.");
        }

        // Создаем и сохраняем заказ
        Order order = new Order();
        order.setFlight(flight);
        order.setUser(user);
        return orderRepository.save(order);
    }



}
