package com.airport.persistence;

import com.airport.config.SpringConfig;
import com.airport.domain.Flight;
import com.airport.domain.TypeCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {SpringConfig.class})
public class FlightRepositoryTest {

    @Autowired
    private FlightRepository flightRepo;

    @Test
    public void testFindAvailableFlights() {
        List<Flight> flights = flightRepo.findByIsAvailable(true);
        assertTrue("Should find at least one available flight", flights.size() > 0);
        flights.forEach(System.out::println);
    }

    @Test
    public void testAddFlight() {
        Flight flight = new Flight("AA103TEST", "New York", true, BigDecimal.valueOf(199.99), TypeCategory.INTERNATIONAL);
        flightRepo.save(flight);
        assertNotNull("Flight ID should not be null after saving", flight.getId());
    }

    @Test
    public void testFindByTypeCategory() {
        List<Flight> flights = flightRepo.findByTypeCategory(TypeCategory.INTERNATIONAL);
        assertTrue("Should find at least one international flight", flights.size() > 0);
        flights.forEach(System.out::println);
    }
}
