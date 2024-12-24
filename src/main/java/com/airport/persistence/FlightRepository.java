package com.airport.persistence;

import com.airport.domain.Flight;
import com.airport.domain.TypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByIsAvailable(boolean isAvailable);
    List<Flight> findByTypeCategory(TypeCategory typeCategory);
    List<Flight> findByIsAvailableAndTypeCategory(boolean isAvailable, TypeCategory typeCategory);

    // Новый метод для поиска по пункту назначения, игнорируя регистр
    List<Flight> findByDestinationContainingIgnoreCase(String destination);

    // Метод для подсчета количества заказов по каждому рейсу
    @Query("SELECT f.flightNumber, COUNT(o) FROM Order o JOIN o.flight f GROUP BY f.flightNumber")
    List<Object[]> countByFlightGroupByFlight();
}
