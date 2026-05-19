package com.airline.controller.api;

import com.airline.model.Flight;
import com.airline.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightApiController {

    private final FlightService flightService;

    @Autowired
    public FlightApiController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFlightById(@PathVariable int id) {
        try {
            Flight flight = flightService.getFlightDetails(id);
            return ResponseEntity.ok(flight);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addFlight(@RequestBody FlightRequest request) {
        try {
            Flight flight = flightService.addFlight(
                request.getFlightNumber(),
                request.getSource(),
                request.getDestination(),
                request.getDepartureTime(),
                request.getArrivalTime(),
                request.getTotalSeats(),
                request.getPrice(),
                request.getStatus() != null ? request.getStatus() : "ON TIME"
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(flight);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFlight(@PathVariable int id, @RequestBody FlightRequest request) {
        try {
            boolean success = flightService.updateFlight(
                id,
                request.getSource(),
                request.getDestination(),
                request.getDepartureTime(),
                request.getArrivalTime(),
                request.getTotalSeats(),
                request.getAvailableSeats(),
                request.getPrice(),
                request.getStatus()
            );
            if (success) {
                return ResponseEntity.ok("Flight successfully updated.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flight.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlight(@PathVariable int id) {
        try {
            boolean success = flightService.deleteFlight(id);
            if (success) {
                return ResponseEntity.ok("Flight deleted successfully.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete flight.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public static class FlightRequest {
        private String flightNumber;
        private String source;
        private String destination;
        private String departureTime;
        private String arrivalTime;
        private int totalSeats;
        private int availableSeats;
        private double price;
        private String status;

        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }

        public String getDepartureTime() { return departureTime; }
        public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

        public String getArrivalTime() { return arrivalTime; }
        public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

        public int getTotalSeats() { return totalSeats; }
        public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
