/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.repository;

import com.airline.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    Optional<Reservation> findByPnr(String pnr);
    List<Reservation> findByUserId(int userId);
    List<Reservation> findByFlightIdAndStatus(int flightId, String status);
}
