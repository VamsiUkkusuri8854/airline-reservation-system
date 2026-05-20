/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

/**
 * Represents a Ticket/Flight Reservation mapped to reservations table.
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String pnr;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "flight_id", nullable = false)
    private int flightId;

    @Column(name = "booking_date", insertable = false, updatable = false)
    private Timestamp bookingDate;

    @Column(name = "seats_booked")
    private int seatsBooked;

    @Column(name = "seats")
    private String seats = "";

    private String status; // CONFIRMED or CANCELLED

    @Column(name = "total_amount")
    private double totalAmount;

    // Helper properties for display purposes annotated with @Transient (not written to db column)
    @Transient
    private String passengerName;
    @Transient
    private String flightNumber;
    @Transient
    private String source;
    @Transient
    private String destination;
    @Transient
    private Timestamp departureTime;

    public Reservation() {}

    public Reservation(String pnr, int userId, int flightId, int seatsBooked, String status, double totalAmount) {
        this.pnr = pnr;
        this.userId = userId;
        this.flightId = flightId;
        this.seatsBooked = seatsBooked;
        this.status = status;
        this.totalAmount = totalAmount;
        this.seats = "";
    }

    public Reservation(String pnr, int userId, int flightId, int seatsBooked, String seats, String status, double totalAmount) {
        this.pnr = pnr;
        this.userId = userId;
        this.flightId = flightId;
        this.seatsBooked = seatsBooked;
        this.seats = (seats != null) ? seats : "";
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public Reservation(int id, String pnr, int userId, int flightId, Timestamp bookingDate, int seatsBooked, String status, double totalAmount) {
        this.id = id;
        this.pnr = pnr;
        this.userId = userId;
        this.flightId = flightId;
        this.bookingDate = bookingDate;
        this.seatsBooked = seatsBooked;
        this.status = status;
        this.totalAmount = totalAmount;
        this.seats = "";
    }

    public Reservation(int id, String pnr, int userId, int flightId, Timestamp bookingDate, int seatsBooked, String seats, String status, double totalAmount) {
        this.id = id;
        this.pnr = pnr;
        this.userId = userId;
        this.flightId = flightId;
        this.bookingDate = bookingDate;
        this.seatsBooked = seatsBooked;
        this.seats = (seats != null) ? seats : "";
        this.status = status;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", pnr='" + pnr + '\'' +
                ", userId=" + userId +
                ", flightId=" + flightId +
                ", bookingDate=" + bookingDate +
                ", seatsBooked=" + seatsBooked +
                ", seats='" + seats + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
