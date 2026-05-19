package com.airline.dao.impl;

import com.airline.dao.FlightDAO;
import com.airline.model.Flight;
import com.airline.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAOImpl implements FlightDAO {

    @Override
    public List<Flight> getAllFlights() throws SQLException {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                flights.add(mapRowToFlight(rs));
            }
        }
        return flights;
    }

    @Override
    public Flight getFlightById(int id) throws SQLException {
        String sql = "SELECT * FROM flights WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFlight(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean addFlight(Flight flight) throws SQLException {
        String sql = "INSERT INTO flights (flight_number, source, destination, departure_time, arrival_time, total_seats, available_seats, price, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getSource());
            ps.setString(3, flight.getDestination());
            ps.setTimestamp(4, flight.getDepartureTime());
            ps.setTimestamp(5, flight.getArrivalTime());
            ps.setInt(6, flight.getTotalSeats());
            ps.setInt(7, flight.getAvailableSeats());
            ps.setDouble(8, flight.getPrice());
            ps.setString(9, flight.getStatus() != null ? flight.getStatus() : "ON TIME");
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateFlight(Flight flight) throws SQLException {
        String sql = "UPDATE flights SET flight_number = ?, source = ?, destination = ?, departure_time = ?, arrival_time = ?, total_seats = ?, available_seats = ?, price = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getSource());
            ps.setString(3, flight.getDestination());
            ps.setTimestamp(4, flight.getDepartureTime());
            ps.setTimestamp(5, flight.getArrivalTime());
            ps.setInt(6, flight.getTotalSeats());
            ps.setInt(7, flight.getAvailableSeats());
            ps.setDouble(8, flight.getPrice());
            ps.setString(9, flight.getStatus() != null ? flight.getStatus() : "ON TIME");
            ps.setInt(10, flight.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteFlight(int id) throws SQLException {
        String sql = "DELETE FROM flights WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsByFlightNumber(String flightNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM flights WHERE flight_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flightNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public boolean existsByFlightNumberExceptId(String flightNumber, int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM flights WHERE flight_number = ? AND id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flightNumber);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Flight mapRowToFlight(ResultSet rs) throws SQLException {
        return new Flight(
            rs.getInt("id"),
            rs.getString("flight_number"),
            rs.getString("source"),
            rs.getString("destination"),
            rs.getTimestamp("departure_time"),
            rs.getTimestamp("arrival_time"),
            rs.getInt("total_seats"),
            rs.getInt("available_seats"),
            rs.getDouble("price"),
            rs.getString("status")
        );
    }
}
