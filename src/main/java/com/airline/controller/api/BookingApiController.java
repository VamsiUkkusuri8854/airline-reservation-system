/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.controller.api;

import com.airline.model.Reservation;
import com.airline.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    private final ReservationService reservationService;

    @Autowired
    public BookingApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable int id) {
        try {
            Reservation res = reservationService.getReservationById(id);
            if (res == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/pnr/{pnr}")
    public ResponseEntity<?> getReservationByPnr(@PathVariable String pnr) {
        try {
            Reservation res = reservationService.getReservationByPnr(pnr);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/flight/{flightId}/seats")
    public ResponseEntity<?> getBookedSeats(@PathVariable int flightId) {
        try {
            return ResponseEntity.ok(reservationService.getBookedSeatsForFlight(flightId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest request) {
        try {
            Reservation res = reservationService.bookTicket(
                request.getUserId(),
                request.getFlightId(),
                request.getSeatsToBook(),
                request.getSeats(),
                request.getPaymentMethod()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/pnr/{pnr}")
    public ResponseEntity<?> cancelTicket(@PathVariable String pnr, @RequestParam int userId, @RequestParam(defaultValue = "false") boolean isAdmin) {
        try {
            double refund = reservationService.cancelTicket(pnr, userId, isAdmin);
            return ResponseEntity.ok("Booking successfully cancelled. Refund amount processed: $" + refund);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public static class BookingRequest {
        private int userId;
        private int flightId;
        private int seatsToBook;
        private String seats;
        private String paymentMethod;

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public int getFlightId() { return flightId; }
        public void setFlightId(int flightId) { this.flightId = flightId; }

        public int getSeatsToBook() { return seatsToBook; }
        public void setSeatsToBook(int seatsToBook) { this.seatsToBook = seatsToBook; }

        public String getSeats() { return seats; }
        public void setSeats(String seats) { this.seats = seats; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
}
