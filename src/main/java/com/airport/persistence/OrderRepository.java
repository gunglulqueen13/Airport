package com.airport.persistence;

import com.airport.domain.Order;
import com.airport.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Найти все заказы пользователя
    List<Order> findByUser(User user);

    // Подсчитать общее количество заказов
    long count();

    // Подсчитать количество заказов с отменой
    long countByCancellationReasonIsNotNull();

    // Группировка заказов по причинам отмены и подсчет их
    @Query("SELECT o.cancellationReason, COUNT(o) FROM Order o GROUP BY o.cancellationReason")
    List<Object[]> countByCancellationReasonGroupByReason();

    // Группировка заказов по городам (destination) и подсчет их
    @Query("SELECT o.flight.destination, COUNT(o) FROM Order o GROUP BY o.flight.destination")
    List<Object[]> countByCityGroupByCity();  // Используем destination
}
