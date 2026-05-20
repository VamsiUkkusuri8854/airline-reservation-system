/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.service;

import com.airline.exception.FlightNotFoundException;
import com.airline.exception.InvalidInputException;
import com.airline.model.Flight;
import com.airline.repository.FlightRepository;
import com.airline.util.InputValidator;
import com.airline.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Service class managing Flight schedules, listings, and adjustments using Spring Data repositories.
 */
@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final com.airline.repository.ReservationRepository reservationRepository;
    private final com.airline.repository.UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public FlightService(FlightRepository flightRepository,
                         com.airline.repository.ReservationRepository reservationRepository,
                         com.airline.repository.UserRepository userRepository,
                         EmailService emailService) {
        this.flightRepository = flightRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Schedules a new flight.
     */
    @Transactional
    public Flight addFlight(String flightNumber, String source, String destination, 
                             String departureTimeStr, String arrivalTimeStr, int totalSeats, double price) {
        return addFlight(flightNumber, source, destination, departureTimeStr, arrivalTimeStr, totalSeats, price, "ON TIME");
    }

    @Transactional
    public Flight addFlight(String flightNumber, String source, String destination, 
                             String departureTimeStr, String arrivalTimeStr, int totalSeats, double price, String status) {
        
        if (flightNumber == null || flightNumber.trim().isEmpty() ||
            source == null || source.trim().isEmpty() ||
            destination == null || destination.trim().isEmpty()) {
            throw new InvalidInputException("Flight number, source, and destination are mandatory.");
        }

        if (!InputValidator.isValidDateTime(departureTimeStr)) {
            throw new InvalidInputException("Invalid departure time formatting. Must match 'yyyy-MM-dd HH:mm:ss'.");
        }

        if (!InputValidator.isValidDateTime(arrivalTimeStr)) {
            throw new InvalidInputException("Invalid arrival time formatting. Must match 'yyyy-MM-dd HH:mm:ss'.");
        }

        Timestamp departure = InputValidator.toTimestamp(departureTimeStr);
        Timestamp arrival = InputValidator.toTimestamp(arrivalTimeStr);

        if (departure == null || arrival == null) {
            throw new InvalidInputException("Date-time parsing resulted in null timestamp.");
        }

        if (arrival.before(departure)) {
            throw new InvalidInputException("Arrival time cannot occur before departure time.");
        }

        if (totalSeats <= 0) {
            throw new InvalidInputException("Total seat capacity must be at least 1.");
        }

        if (price <= 0.0) {
            throw new InvalidInputException("Ticket price must be greater than $0.00.");
        }

        if (flightRepository.findByFlightNumber(flightNumber).isPresent()) {
            throw new InvalidInputException("Flight number '" + flightNumber + "' already exists.");
        }

        Flight flight = new Flight(flightNumber, source, destination, departure, arrival, totalSeats, totalSeats, price, status);
        Flight savedFlight = flightRepository.save(flight);

        Logger.info("Scheduled new flight: " + flightNumber + " (" + source + " -> " + destination + ") Status: " + status);
        return savedFlight;
    }

    /**
     * Updates an existing flight schedule details.
     */
    @Transactional
    public boolean updateFlight(int id, String source, String destination, 
                                String departureTimeStr, String arrivalTimeStr, 
                                int totalSeats, int availableSeats, double price) {
        Optional<Flight> existing = flightRepository.findById(id);
        String currentStatus = existing.map(Flight::getStatus).orElse("ON TIME");
        return updateFlight(id, source, destination, departureTimeStr, arrivalTimeStr, totalSeats, availableSeats, price, currentStatus);
    }

    @Transactional
    public boolean updateFlight(int id, String source, String destination, 
                                String departureTimeStr, String arrivalTimeStr, 
                                int totalSeats, int availableSeats, double price, String status) {
        
        if (source == null || source.trim().isEmpty() ||
            destination == null || destination.trim().isEmpty()) {
            throw new InvalidInputException("Source and destination cities cannot be blank.");
        }

        if (!InputValidator.isValidDateTime(departureTimeStr) || !InputValidator.isValidDateTime(arrivalTimeStr)) {
            throw new InvalidInputException("Date-time must match format 'yyyy-MM-dd HH:mm:ss'.");
        }

        Timestamp departure = InputValidator.toTimestamp(departureTimeStr);
        Timestamp arrival = InputValidator.toTimestamp(arrivalTimeStr);

        if (departure == null || arrival == null) {
            throw new InvalidInputException("Date parsing failed.");
        }

        if (arrival.before(departure)) {
            throw new InvalidInputException("Arrival time cannot occur before departure.");
        }

        if (totalSeats <= 0 || availableSeats < 0 || availableSeats > totalSeats) {
            throw new InvalidInputException("Invalid seat counts. Available seats must be between 0 and total seats.");
        }

        if (price <= 0.0) {
            throw new InvalidInputException("Ticket price must be positive.");
        }

        Optional<Flight> flightOpt = flightRepository.findById(id);
        if (!flightOpt.isPresent()) {
            throw new FlightNotFoundException("Flight schedule with ID " + id + " does not exist.");
        }

        Flight flight = flightOpt.get();
        String oldStatus = flight.getStatus();
        
        flight.setSource(source);
        flight.setDestination(destination);
        flight.setDepartureTime(departure);
        flight.setArrivalTime(arrival);
        flight.setTotalSeats(totalSeats);
        flight.setAvailableSeats(availableSeats);
        flight.setPrice(price);
        
        if (status != null && !status.trim().isEmpty()) {
            flight.setStatus(status.trim());
        }

        flightRepository.save(flight);
        Logger.info("Updated flight ID " + id + " successfully.");

        // If flight status has changed (e.g. DELAYED or CANCELLED), notify all confirmed passengers!
        String newStatus = flight.getStatus();
        if (!oldStatus.equalsIgnoreCase(newStatus)) {
            try {
                java.util.List<com.airline.model.Reservation> reservations = reservationRepository.findByFlightIdAndStatus(id, "CONFIRMED");
                for (com.airline.model.Reservation res : reservations) {
                    userRepository.findById(res.getUserId()).ifPresent(passenger -> {
                        try {
                            emailService.sendFlightStatusUpdate(passenger, flight, oldStatus, newStatus);
                        } catch (Exception e) {
                            Logger.error("Failed to send simulated status update email: " + e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                Logger.error("Error in flight status notification loop: " + e.getMessage());
            }
        }

        return true;
    }

    /**
     * Cancels / deletes a scheduled flight from index.
     */
    @Transactional
    public boolean deleteFlight(int id) {
        Optional<Flight> flightOpt = flightRepository.findById(id);
        if (!flightOpt.isPresent()) {
            throw new FlightNotFoundException("Flight schedule with ID " + id + " does not exist.");
        }
        flightRepository.deleteById(id);
        Logger.info("Deleted flight ID " + id + " successfully.");
        return true;
    }

    /**
     * Search routes.
     */
    public List<Flight> searchFlights(String source, String destination) {
        return flightRepository.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination);
    }

    /**
     * Lists all flights.
     */
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    /**
     * Retrieve single flight profile details.
     */
    public Flight getFlightDetails(int flightId) {
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (!flight.isPresent()) {
            throw new FlightNotFoundException("Flight schedule with ID " + flightId + " does not exist.");
        }
        return flight.get();
    }
}
