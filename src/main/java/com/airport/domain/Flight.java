package com.airport.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "Flight")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String flightNumber;
    private String destination;
    private boolean isAvailable;
    @Column(name = "cost")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TypeCategory typeCategory;

    public Flight() {
    }

    public Flight(String flightNumber, String destination, boolean isAvailable, BigDecimal price, TypeCategory typeCategory) {
        this.flightNumber = flightNumber;
        this.destination = destination;
        this.isAvailable = isAvailable;
        this.price = price;
        this.typeCategory = typeCategory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TypeCategory getTypeCategory() {
        return typeCategory;
    }

    public void setTypeCategory(TypeCategory typeCategory) {
        this.typeCategory = typeCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Flight) {
            Flight other = (Flight) o;
            return Objects.equals(flightNumber, other.flightNumber) &&
                    Objects.equals(destination, other.destination);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightNumber, destination);
    }

    @Override
    public String toString() {
        return "Flight [id=" + id + ", flightNumber=" + flightNumber + ", destination=" + destination +
                ", isAvailable=" + isAvailable + ", price=" + price + ", typeCategory=" + typeCategory + "]";
    }
}
