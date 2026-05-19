package com.airline.dao;

import com.airline.model.Flight;
import java.sql.SQLException;
import java.util.List;

public interface FlightDAO {
    List<Flight> getAllFlights() throws SQLException;
    Flight getFlightById(int id) throws SQLException;
    boolean addFlight(Flight flight) throws SQLException;
    boolean updateFlight(Flight flight) throws SQLException;
    boolean deleteFlight(int id) throws SQLException;
    boolean existsByFlightNumber(String flightNumber) throws SQLException;
    boolean existsByFlightNumberExceptId(String flightNumber, int id) throws SQLException;
}
