/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.controller;

import com.airline.exception.AirlineException;
import com.airline.model.Flight;
import com.airline.model.Reservation;
import com.airline.model.User;
import com.airline.service.FlightService;
import com.airline.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FlightController {

    private static final List<String> AIRPORTS = java.util.Arrays.asList(
        "JFK - John F. Kennedy International Airport - New York",
        "LHR - Heathrow Airport - London",
        "SFO - San Francisco International Airport - San Francisco",
        "CDG - Charles de Gaulle Airport - Paris",
        "DXB - Dubai International Airport - Dubai",
        "HND - Tokyo Haneda Airport - Tokyo",
        "SYD - Sydney Kingsford Smith Airport - Sydney",
        "ORD - O'Hare International Airport - Chicago",
        "LAX - Los Angeles International Airport - Los Angeles",
        "FRA - Frankfurt Airport - Frankfurt"
    );

    private final FlightService flightService;
    private final ReservationService reservationService;
    private final com.airline.service.PdfGeneratorService pdfGeneratorService;

    @Autowired
    public FlightController(FlightService flightService, 
                            ReservationService reservationService,
                            com.airline.service.PdfGeneratorService pdfGeneratorService) {
        this.flightService = flightService;
        this.reservationService = reservationService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @PostMapping("/flights/search")
    public String searchFlights(@RequestParam String source,
                                 @RequestParam String destination,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("user");
        List<Flight> searchResults = flightService.searchFlights(source, destination);

        model.addAttribute("user", user);
        model.addAttribute("flights", searchResults);
        model.addAttribute("searched", true);
        model.addAttribute("source", source);
        model.addAttribute("destination", destination);

        if (user != null) {
            // Populate metrics so panel remains intact
            List<Reservation> history = reservationService.getBookingHistory(user.getId());
            double totalSpent = history.stream()
                    .filter(r -> "CONFIRMED".equals(r.getStatus()))
                    .mapToDouble(Reservation::getTotalAmount)
                    .sum();
            model.addAttribute("totalBookings", history.size());
            model.addAttribute("activeFlightsCount", flightService.getAllFlights().size());
            model.addAttribute("totalSpent", totalSpent);
            model.addAttribute("reservations", history);
            return "customer-dashboard";
        }
        return "index";
    }

    @GetMapping("/live-flights")
    public String showLiveFlights(@RequestParam(required = false) String source,
                                  @RequestParam(required = false) String destination,
                                  @RequestParam(required = false) String airline,
                                  @RequestParam(required = false) String status,
                                  HttpSession session,
                                  Model model) {
        // Pass session user for session-aware navigation
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        // Fetch flights (both API and manual fallback are supported via service)
        List<Flight> allFlights = flightService.getAllFlights();
        
        if (source != null && !source.trim().isEmpty()) {
            allFlights = allFlights.stream().filter(f -> f.getSource().toLowerCase().contains(source.toLowerCase())).collect(Collectors.toList());
        }
        if (destination != null && !destination.trim().isEmpty()) {
            allFlights = allFlights.stream().filter(f -> f.getDestination().toLowerCase().contains(destination.toLowerCase())).collect(Collectors.toList());
        }
        if (airline != null && !airline.trim().isEmpty()) {
            allFlights = allFlights.stream().filter(f -> f.getAirlineName() != null && f.getAirlineName().toLowerCase().contains(airline.toLowerCase())).collect(Collectors.toList());
        }
        if (status != null && !status.trim().isEmpty() && !status.equals("ALL")) {
            allFlights = allFlights.stream().filter(f -> f.getStatus() != null && f.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
        }

        model.addAttribute("flights", allFlights);
        model.addAttribute("sourceParam", source);
        model.addAttribute("destinationParam", destination);
        model.addAttribute("airlineParam", airline);
        model.addAttribute("statusParam", status);
        
        return "live-flights";
    }

    @GetMapping("/api/airports/search")
    @ResponseBody
    public List<String> searchAirports(@RequestParam String query) {
        String lowerQuery = query.toLowerCase();
        return AIRPORTS.stream()
                .filter(a -> a.toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    @PostMapping("/admin/flights/add")
    public String addFlight(@RequestParam String flightNumber,
                            @RequestParam String source,
                            @RequestParam String destination,
                            @RequestParam String departureTime,
                            @RequestParam String arrivalTime,
                            @RequestParam int totalSeats,
                            @RequestParam double price,
                            @RequestParam(required = false, defaultValue = "ON TIME") String status,
                            HttpSession session,
                            Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        try {
            flightService.addFlight(flightNumber, source, destination, departureTime, arrivalTime, totalSeats, price, status);
            return "redirect:/admin/dashboard?successAdd=true";
        } catch (AirlineException e) {
            return "redirect:/admin/dashboard?errorAdd=" + e.getMessage();
        }
    }

    @PostMapping("/admin/flights/update")
    public String updateFlight(@RequestParam int id,
                               @RequestParam String source,
                               @RequestParam String destination,
                               @RequestParam String departureTime,
                               @RequestParam String arrivalTime,
                               @RequestParam int totalSeats,
                               @RequestParam int availableSeats,
                               @RequestParam double price,
                               @RequestParam String status,
                               HttpSession session,
                               Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        try {
            flightService.updateFlight(id, source, destination, departureTime, arrivalTime, totalSeats, availableSeats, price, status);
            return "redirect:/admin/dashboard?successUpdate=true";
        } catch (AirlineException e) {
            return "redirect:/admin/dashboard?errorUpdate=" + e.getMessage();
        }
    }

    @PostMapping("/admin/flights/delete")
    public String deleteFlight(@RequestParam int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        try {
            flightService.deleteFlight(id);
            return "redirect:/admin/dashboard?successDel=true";
        } catch (AirlineException e) {
            return "redirect:/admin/dashboard?errorDel=" + e.getMessage();
        }
    }

    @GetMapping("/booking")
    public String showBookingPage(@RequestParam int flightId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Flight flight = flightService.getFlightDetails(flightId);
            model.addAttribute("user", user);
            model.addAttribute("flight", flight);
            return "book-ticket";
        } catch (AirlineException e) {
            return "redirect:/customer/dashboard?error=" + e.getMessage();
        }
    }

    @PostMapping("/booking/process")
    public String processBooking(@RequestParam int flightId,
                                 @RequestParam int seats,
                                 @RequestParam(required = false, defaultValue = "") String seatList,
                                 @RequestParam String paymentMethod,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Reservation res = reservationService.bookTicket(user.getId(), flightId, seats, seatList, paymentMethod);
            model.addAttribute("user", user);
            model.addAttribute("reservation", res);
            return "payment-receipt";
        } catch (AirlineException e) {
            model.addAttribute("error", e.getMessage());
            try {
                Flight flight = flightService.getFlightDetails(flightId);
                model.addAttribute("flight", flight);
            } catch (Exception ignored) {}
            return "book-ticket";
        }
    }

    @PostMapping("/booking/cancel")
    public String cancelBooking(@RequestParam String pnr, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        boolean isAdmin = "ADMIN".equals(user.getRole());
        try {
            reservationService.cancelTicket(pnr, user.getId(), isAdmin);
            return isAdmin ? "redirect:/admin/dashboard?successCancel=true" : "redirect:/customer/dashboard?successCancel=true";
        } catch (AirlineException e) {
            return isAdmin ? "redirect:/admin/dashboard?errorCancel=" + e.getMessage() : "redirect:/customer/dashboard?errorCancel=" + e.getMessage();
        }
    }

    @GetMapping("/booking/pdf")
    public org.springframework.http.ResponseEntity<byte[]> downloadBoardingPass(@RequestParam String pnr, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Reservation res = reservationService.getReservationByPnr(pnr);
            // Secure security guard check: customer can only download their own ticket
            if (!"ADMIN".equals(user.getRole()) && res.getUserId() != user.getId()) {
                return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
            }

            byte[] pdfBytes = pdfGeneratorService.generateBoardingPassPdf(res);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "BoardingPass_" + pnr + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new org.springframework.http.ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);

        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
