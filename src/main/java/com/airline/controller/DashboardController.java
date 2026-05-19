package com.airline.controller;

import com.airline.model.Flight;
import com.airline.model.Payment;
import com.airline.model.Reservation;
import com.airline.model.User;
import com.airline.service.FlightService;
import com.airline.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class DashboardController {

    private final FlightService flightService;
    private final ReservationService reservationService;
    private final com.airline.service.UserService userService;

    @Autowired
    public DashboardController(FlightService flightService, ReservationService reservationService, com.airline.service.UserService userService) {
        this.flightService = flightService;
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Flight> flights = flightService.getAllFlights();
        List<Reservation> reservations = reservationService.getAllReservations();
        List<Payment> payments = reservationService.getAllPayments();

        // Calculate admin metrics
        double grossRevenue = payments.stream()
                .filter(p -> "SUCCESS".equals(p.getPaymentStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        long registeredUsersCount = userService.getTotalUsersCount();
        long activeFlightsCount = flights.stream().filter(f -> !"CANCELLED".equalsIgnoreCase(f.getStatus())).count();
        long cancellationCount = reservations.stream().filter(r -> "CANCELLED".equalsIgnoreCase(r.getStatus())).count();

        // Calculate Payment Methods split counts
        long cardCount = payments.stream().filter(p -> "CARD".equals(p.getPaymentMethod())).count();
        long upiCount = payments.stream().filter(p -> "UPI".equals(p.getPaymentMethod())).count();
        long bankingCount = payments.stream().filter(p -> "NET_BANKING".equals(p.getPaymentMethod())).count();

        // Generate Occupancy metrics for charts
        java.util.List<String> flightNamesList = new java.util.ArrayList<>();
        java.util.List<String> flightOccupancyList = new java.util.ArrayList<>();
        for (Flight f : flights) {
            flightNamesList.add(f.getFlightNumber());
            int total = f.getTotalSeats();
            int occupied = total - f.getAvailableSeats();
            int pct = total > 0 ? (occupied * 100 / total) : 0;
            flightOccupancyList.add(String.valueOf(pct));
        }
        
        String flightLabels = String.join(",", flightNamesList);
        String occupancyData = String.join(",", flightOccupancyList);

        model.addAttribute("user", user);
        model.addAttribute("flights", flights);
        model.addAttribute("reservations", reservations);
        model.addAttribute("payments", payments);

        model.addAttribute("totalRoutes", flights.size());
        model.addAttribute("totalTickets", reservations.size());
        model.addAttribute("grossRevenue", grossRevenue);
        model.addAttribute("registeredUsersCount", registeredUsersCount);
        model.addAttribute("activeFlightsCount", activeFlightsCount);
        model.addAttribute("cancellationCount", cancellationCount);
        
        // Chart metrics attributes
        model.addAttribute("cardCount", cardCount);
        model.addAttribute("upiCount", upiCount);
        model.addAttribute("bankingCount", bankingCount);
        model.addAttribute("flightLabels", flightLabels);
        model.addAttribute("occupancyData", occupancyData);

        return "admin-dashboard";
    }

    @GetMapping("/customer/dashboard")
    public String showCustomerDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Flight> flights = flightService.getAllFlights();
        List<Reservation> history = reservationService.getBookingHistory(user.getId());

        // Calculate user metrics
        double totalSpent = history.stream()
                .filter(r -> "CONFIRMED".equals(r.getStatus()))
                .mapToDouble(Reservation::getTotalAmount)
                .sum();

        model.addAttribute("user", user);
        model.addAttribute("flights", flights);
        model.addAttribute("reservations", history);

        model.addAttribute("totalBookings", history.size());
        model.addAttribute("activeFlightsCount", flights.size());
        model.addAttribute("totalSpent", totalSpent);

        return "customer-dashboard";
    }
}
