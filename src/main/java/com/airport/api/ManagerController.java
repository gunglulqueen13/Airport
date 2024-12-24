package com.airport.api;

import com.airport.persistence.OrderRepository;
import com.airport.persistence.FlightRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final OrderRepository orderRepository;
    private final FlightRepository flightRepository;

    public ManagerController(OrderRepository orderRepository, FlightRepository flightRepository) {
        this.orderRepository = orderRepository;
        this.flightRepository = flightRepository;
    }

    // Получение всех билетов всех пользователей
    @GetMapping("/manager_tickets")
    public List<Map<String, Object>> getAllTickets() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    String cancellationReason = order.getCancellationReason();
                    return Map.of(
                            "id", order.getId(),
                            "user", Map.of(
                                    "userId", order.getUser().getId(),
                                    "username", order.getUser().getUsername()
                            ),
                            "flight", Map.of(
                                    "flightNumber", order.getFlight().getFlightNumber(),
                                    "destination", order.getFlight().getDestination(),
                                    "price", order.getFlight().getPrice(),
                                    "typeCategory", order.getFlight().getTypeCategory(),
                                    "available", order.getFlight().isAvailable()
                            ),
                            "cancellationReason", cancellationReason != null ? cancellationReason : "No cancellation reason"
                    );
                })
                .toList();
    }

    // Получение статистики
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        long totalTickets = orderRepository.count();
        long cancelledTickets = orderRepository.countByCancellationReasonIsNotNull();
        long activeTickets = totalTickets - cancelledTickets;

        // Статистика по причинам отмены
        List<Object[]> cancellationReasonsList = orderRepository.countByCancellationReasonGroupByReason();
        Map<String, Long> cancellationReasons = cancellationReasonsList.stream()
                .filter(obj -> obj[0] != null)
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> (Long) obj[1]
                ));

        // Статистика по рейсам
        List<Object[]> flightTicketsList = flightRepository.countByFlightGroupByFlight();
        Map<String, Long> flightTickets = flightTicketsList.stream()
                .filter(obj -> obj[0] != null)
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> (Long) obj[1]
                ));

        // Статистика по городам
        List<Object[]> cityTicketsList = orderRepository.countByCityGroupByCity();  // Новая статистика
        Map<String, Long> cityTickets = cityTicketsList.stream()
                .filter(obj -> obj[0] != null)
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> (Long) obj[1]
                ));

        return Map.of(
                "totalTickets", totalTickets,
                "cancelledTickets", cancelledTickets,
                "activeTickets", activeTickets,
                "cancellationReasons", cancellationReasons,
                "flightTickets", flightTickets,
                "cityTickets", cityTickets  // Добавляем статистику по городам
        );
    }

}
