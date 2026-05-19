package com.airline.service;

import com.airline.exception.FlightNotFoundException;
import com.airline.exception.InvalidInputException;
import com.airline.exception.PaymentFailedException;
import com.airline.exception.SeatsNotAvailableException;
import com.airline.model.Flight;
import com.airline.model.Payment;
import com.airline.model.Reservation;
import com.airline.model.User;
import com.airline.repository.FlightRepository;
import com.airline.repository.PaymentRepository;
import com.airline.repository.ReservationRepository;
import com.airline.repository.UserRepository;
import com.airline.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service class managing ticket booking transactions, payments, and cancellations.
 * Employs Spring managed transactions for atomic database operations.
 */
@Service
public class ReservationService {

    private final FlightRepository flightRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public ReservationService(FlightRepository flightRepository,
                              ReservationRepository reservationRepository,
                              PaymentRepository paymentRepository,
                              UserRepository userRepository,
                              EmailService emailService) {
        this.flightRepository = flightRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Executes atomic ticket reservation and payment processing.
     */
    @Transactional
    public Reservation bookTicket(int userId, int flightId, int seatsToBook, String paymentMethod) {
        return bookTicket(userId, flightId, seatsToBook, "", paymentMethod);
    }

    @Transactional
    public Reservation bookTicket(int userId, int flightId, int seatsToBook, String seats, String paymentMethod) {
        if (seatsToBook <= 0) {
            throw new InvalidInputException("Seat count must be at least 1.");
        }
        if (paymentMethod == null || (!paymentMethod.equals("CARD") && !paymentMethod.equals("UPI") && !paymentMethod.equals("NET_BANKING"))) {
            throw new InvalidInputException("Invalid payment method selected.");
        }

        // 1. Validate flight existence
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (!flightOpt.isPresent()) {
            throw new FlightNotFoundException("Flight schedule with ID " + flightId + " does not exist.");
        }
        Flight flight = flightOpt.get();

        // 2. Validate seat availability count
        if (flight.getAvailableSeats() < seatsToBook) {
            throw new SeatsNotAvailableException("Insufficient seats. Requested: " + seatsToBook + ", Available: " + flight.getAvailableSeats());
        }

        // 3. Double-Booking Prevention: Validate specific visual seats are not already taken
        if (seats != null && !seats.trim().isEmpty()) {
            List<String> alreadyBooked = getBookedSeatsForFlight(flightId);
            String[] requested = seats.split(",");
            for (String req : requested) {
                String trimmed = req.trim();
                if (alreadyBooked.contains(trimmed)) {
                    throw new SeatsNotAvailableException("Seat " + trimmed + " is already booked by another passenger. Please select another seat.");
                }
            }
        }

        // 4. Decrement available seats count
        flight.setAvailableSeats(flight.getAvailableSeats() - seatsToBook);
        flightRepository.save(flight);

        // 5. Create PNR reference and calculate price
        String pnr = generatePNR();
        double totalAmount = seatsToBook * flight.getPrice();

        // 6. Save reservation
        Reservation reservation = new Reservation(pnr, userId, flightId, seatsToBook, seats, "CONFIRMED", totalAmount);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 7. Record transaction payment log
        String txnId = generateTransactionID();
        Payment payment = new Payment(savedReservation.getId(), paymentMethod, "SUCCESS", txnId, totalAmount);
        Payment savedPayment = paymentRepository.save(payment);
        if (savedPayment == null) {
            throw new PaymentFailedException("Payment processing failed. Transaction aborted.");
        }

        Logger.info("Ticket booked successfully! PNR: " + pnr + ", Amount: $" + totalAmount + ", Seats: " + seats);
        
        // Populate display helpers
        User passenger = userRepository.findById(userId).orElse(null);
        populateTransientFields(savedReservation, flight, passenger);
        
        // Dynamic email dispatch
        if (passenger != null) {
            try {
                emailService.sendBookingConfirmation(savedReservation, passenger, flight);
            } catch (Exception e) {
                Logger.error("Failed simulated email confirmation dispatch: " + e.getMessage());
            }
        }
        
        // Simulate console dispatch
        simulateEmailDispatch(savedReservation, passenger, flight);
        
        return savedReservation;
    }

    /**
     * Retrieves a hydrated reservation by its Database ID.
     */
    public Reservation getReservationById(int id) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return null;
        }
        Reservation res = resOpt.get();
        Flight flight = flightRepository.findById(res.getFlightId()).orElse(null);
        User passenger = userRepository.findById(res.getUserId()).orElse(null);
        populateTransientFields(res, flight, passenger);
        return res;
    }

    /**
     * Retrieves a hydrated reservation by PNR reference.
     */
    public Reservation getReservationByPnr(String pnr) {
        if (pnr == null || pnr.trim().isEmpty()) {
            throw new InvalidInputException("PNR reference is required.");
        }
        Optional<Reservation> resOpt = reservationRepository.findByPnr(pnr);
        if (!resOpt.isPresent()) {
            throw new FlightNotFoundException("Booking record with PNR " + pnr + " does not exist.");
        }
        Reservation res = resOpt.get();
        Flight flight = flightRepository.findById(res.getFlightId()).orElse(null);
        User passenger = userRepository.findById(res.getUserId()).orElse(null);
        populateTransientFields(res, flight, passenger);
        return res;
    }

    /**
     * Executes atomic cancellation of a booking and refund processing.
     */
    @Transactional
    public double cancelTicket(String pnr, int userId, boolean isAdmin) {
        if (pnr == null || pnr.trim().isEmpty()) {
            throw new InvalidInputException("PNR reference is required.");
        }

        Optional<Reservation> resOpt = reservationRepository.findByPnr(pnr);
        if (!resOpt.isPresent()) {
            throw new FlightNotFoundException("Booking record with PNR " + pnr + " does not exist.");
        }

        Reservation res = resOpt.get();
        if (!isAdmin && res.getUserId() != userId) {
            throw new InvalidInputException("Access Denied: PNR does not match your active account.");
        }

        if ("CANCELLED".equals(res.getStatus())) {
            throw new InvalidInputException("Reservation is already cancelled.");
        }

        // 1. Update reservation status to CANCELLED
        res.setStatus("CANCELLED");
        reservationRepository.save(res);

        // 2. Restore seat counts to the flight available capacity
        Optional<Flight> flightOpt = flightRepository.findById(res.getFlightId());
        Flight flight = flightOpt.orElse(null);
        if (flight != null) {
            flight.setAvailableSeats(flight.getAvailableSeats() + res.getSeatsBooked());
            flightRepository.save(flight);
        }

        User passenger = userRepository.findById(res.getUserId()).orElse(null);
        populateTransientFields(res, flight, passenger);

        // 3. Simulated Cancellation Email
        if (passenger != null && flight != null) {
            try {
                emailService.sendBookingCancellation(res, passenger, flight);
            } catch (Exception e) {
                Logger.error("Failed simulated email cancellation dispatch: " + e.getMessage());
            }
        }

        Logger.info("Cancelled booking PNR: " + pnr + ". Initiated refund of $" + res.getTotalAmount());
        return res.getTotalAmount();
    }

    /**
     * Aggregates all currently reserved seats coordinates on confirmed flights.
     */
    public List<String> getBookedSeatsForFlight(int flightId) {
        List<Reservation> confirmed = reservationRepository.findByFlightIdAndStatus(flightId, "CONFIRMED");
        List<String> bookedSeats = new ArrayList<>();
        for (Reservation r : confirmed) {
            if (r.getSeats() != null && !r.getSeats().trim().isEmpty()) {
                String[] seatArr = r.getSeats().split(",");
                for (String s : seatArr) {
                    if (!s.trim().isEmpty()) {
                        bookedSeats.add(s.trim());
                    }
                }
            }
        }
        return bookedSeats;
    }

    /**
     * Fetches current seat availability count.
     */
    public int checkSeatAvailability(int flightId) {
        return flightRepository.findById(flightId)
                .map(Flight::getAvailableSeats)
                .orElse(-1);
    }

    /**
     * Retrieves history logs for a specific User.
     */
    public List<Reservation> getBookingHistory(int userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        User passenger = userRepository.findById(userId).orElse(null);
        for (Reservation res : reservations) {
            Flight flight = flightRepository.findById(res.getFlightId()).orElse(null);
            populateTransientFields(res, flight, passenger);
        }
        return reservations;
    }

    /**
     * Retrieves reservation records globally (Admins).
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        for (Reservation res : reservations) {
            Flight flight = flightRepository.findById(res.getFlightId()).orElse(null);
            User passenger = userRepository.findById(res.getUserId()).orElse(null);
            populateTransientFields(res, flight, passenger);
        }
        return reservations;
    }

    /**
     * Retrieves transaction records globally (Admins).
     */
    public List<Payment> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        for (Payment p : payments) {
            reservationRepository.findById(p.getReservationId())
                    .ifPresent(res -> p.setPnr(res.getPnr()));
        }
        return payments;
    }

    private void simulateEmailDispatch(Reservation res, User passenger, Flight flight) {
        if (passenger == null || flight == null) return;
        
        System.out.println("==========================================================================");
        System.out.println("📧  SIMULATED EMAIL DISPATCH SYSTEM (SMTP RELAY SIMULATOR)");
        System.out.println("==========================================================================");
        System.out.println("From: bookings@flyhigh-airlines.com");
        System.out.println("To: " + passenger.getEmail() + " (" + passenger.getName() + ")");
        System.out.println("Subject: ✈  Flight Booking Confirmed - PNR: " + res.getPnr());
        System.out.println("Date: " + new java.util.Date());
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Dear " + passenger.getName() + ",");
        System.out.println("Your flight booking with FlyHigh Airlines has been successfully confirmed.");
        System.out.println("");
        System.out.println("  •  PNR Code:      " + res.getPnr());
        System.out.println("  •  Flight Number:  " + flight.getFlightNumber());
        System.out.println("  •  Route:          " + flight.getSource() + " ➔ " + flight.getDestination());
        System.out.println("  •  Departure:      " + flight.getDepartureTime());
        System.out.println("  •  Seats Booked:   " + res.getSeatsBooked());
        System.out.println("  •  Amount Paid:    $" + res.getTotalAmount());
        System.out.println("");
        System.out.println("Thank you for flying with FlyHigh Airlines!");
        System.out.println("==========================================================================");
    }

    private void populateTransientFields(Reservation res, Flight flight, User passenger) {
        if (flight != null) {
            res.setFlightNumber(flight.getFlightNumber());
            res.setSource(flight.getSource());
            res.setDestination(flight.getDestination());
            res.setDepartureTime(flight.getDepartureTime());
        }
        if (passenger != null) {
            res.setPassengerName(passenger.getName());
        }
    }

    private String generatePNR() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(6);
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateTransactionID() {
        return "TXN" + System.currentTimeMillis() + (100 + new Random().nextInt(900));
    }
}
