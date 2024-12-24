package com.airport.api;

import com.airport.domain.Order;
import com.airport.domain.User;
import com.airport.persistence.OrderRepository;
import com.airport.persistence.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
public class TicketController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public TicketController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // Получение билетов пользователя
    @GetMapping
    public List<Map<String, Object>> getUserTickets(@CookieValue("user_id") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return orderRepository.findByUser(user).stream()
                .map(order -> {
                    String cancellationReason = order.getCancellationReason();
                    return Map.of(
                            "id", order.getId(),  // Добавлен ID заказа
                            "flight", Map.of(
                                    "flightNumber", order.getFlight().getFlightNumber(),
                                    "destination", order.getFlight().getDestination(),
                                    "price", order.getFlight().getPrice(),
                                    "typeCategory", order.getFlight().getTypeCategory(),
                                    "available", order.getFlight().isAvailable()
                            ),
                            // Добавляем проверку на null для cancellationReason
                            "cancellationReason", cancellationReason != null ? cancellationReason : "No cancellation reason"
                    );
                })
                .toList();
    }

    // Удаление билета
    @DeleteMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTicket(
            @PathVariable Long ticketId,
            @CookieValue("user_id") Long userId,
            @RequestBody Map<String, String> payload) {

        String cancellationReason = payload.get("reason");

        Order order = orderRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to cancel this ticket.");
        }

        order.setCancellationReason(cancellationReason);
        orderRepository.save(order);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/manager/{ticketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTicketManager(
            @PathVariable Long ticketId,
            @CookieValue("user_id") Long userId,
            @RequestBody Map<String, String> payload) {

        String cancellationReason = payload.get("reason");

        Order order = orderRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        order.setCancellationReason(cancellationReason);
        orderRepository.save(order);
    }
}
