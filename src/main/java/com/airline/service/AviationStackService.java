/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.service;

import com.airline.model.Flight;
import com.airline.model.dto.AviationStackResponse;
import com.airline.repository.FlightRepository;
import com.airline.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AviationStackService {

    private final RestTemplate restTemplate;
    private final FlightRepository flightRepository;

    @Value("${aviationstack.api.key}")
    private String apiKey;

    private static final String API_URL = "http://api.aviationstack.com/v1/flights?access_key={access_key}&limit=50";

    @Autowired
    public AviationStackService(RestTemplate restTemplate, FlightRepository flightRepository) {
        this.restTemplate = restTemplate;
        this.flightRepository = flightRepository;
    }

    // Run every 5 minutes (300000 ms)
    @Scheduled(fixedRate = 300000)
    public void fetchLiveFlights() {
        Logger.info("Starting scheduled sync of live flights from AviationStack...");
        try {
            AviationStackResponse response = restTemplate.getForObject(API_URL, AviationStackResponse.class, apiKey);

            if (response != null && response.getData() != null) {
                int count = 0;
                for (AviationStackResponse.FlightData apiFlight : response.getData()) {
                    if (apiFlight.getFlight() == null || apiFlight.getDeparture() == null
                            || apiFlight.getArrival() == null) {
                        continue; // Skip incomplete data
                    }

                    String flightNumber = apiFlight.getFlight().getIata();
                    if (flightNumber == null || flightNumber.isEmpty()) {
                        flightNumber = apiFlight.getFlight().getNumber(); // Fallback
                    }

                    if (flightNumber == null || flightNumber.isEmpty()) {
                        continue;
                    }

                    String source = apiFlight.getDeparture().getAirport();
                    String destination = apiFlight.getArrival().getAirport();
                    if (source == null)
                        source = apiFlight.getDeparture().getIata();
                    if (destination == null)
                        destination = apiFlight.getArrival().getIata();

                    String depTimeStr = apiFlight.getDeparture().getScheduled();
                    String arrTimeStr = apiFlight.getArrival().getScheduled();
                    if (depTimeStr == null || arrTimeStr == null)
                        continue;

                    Timestamp depTime = parseIsoDate(depTimeStr);
                    Timestamp arrTime = parseIsoDate(arrTimeStr);

                    String apiStatus = apiFlight.getFlightStatus();
                    String mappedStatus = mapStatus(apiStatus);

                    String airlineName = apiFlight.getAirline() != null ? apiFlight.getAirline().getName()
                            : "Unknown Airline";

                    // Upsert Flight
                    Optional<Flight> existingFlightOpt = flightRepository.findByFlightNumber(flightNumber);
                    if (existingFlightOpt.isPresent()) {
                        Flight existing = existingFlightOpt.get();
                        // Only update if it's an API flight (don't overwrite admin manual flights
                        // unless desired)
                        if (existing.isApiFlight()) {
                            existing.setSource(source);
                            existing.setDestination(destination);
                            existing.setDepartureTime(depTime);
                            existing.setArrivalTime(arrTime);
                            existing.setStatus(mappedStatus);
                            existing.setAirlineName(airlineName);
                            flightRepository.save(existing);
                            count++;
                        }
                    } else {
                        Flight newFlight = new Flight();
                        newFlight.setFlightNumber(flightNumber);
                        newFlight.setSource(source);
                        newFlight.setDestination(destination);
                        newFlight.setDepartureTime(depTime);
                        newFlight.setArrivalTime(arrTime);
                        newFlight.setTotalSeats(200);
                        newFlight.setAvailableSeats(200);
                        newFlight.setPrice(350.00); // Default price for API flights
                        newFlight.setStatus(mappedStatus);
                        newFlight.setAirlineName(airlineName);
                        newFlight.setApiFlight(true);
                        flightRepository.save(newFlight);
                        count++;
                    }
                }
                Logger.info("Successfully synced " + count + " live flights.");
            }
        } catch (Exception e) {
            Logger.error("Error fetching live flights from AviationStack: " + e.getMessage());
        }
    }

    private Timestamp parseIsoDate(String isoDateStr) {
        try {
            OffsetDateTime odt = OffsetDateTime.parse(isoDateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return Timestamp.valueOf(odt.toLocalDateTime());
        } catch (Exception e) {
            return new Timestamp(System.currentTimeMillis());
        }
    }

    private String mapStatus(String apiStatus) {
        if (apiStatus == null)
            return "ON TIME";
        switch (apiStatus.toLowerCase()) {
            case "active":
                return "BOARDING";
            case "scheduled":
                return "ON TIME";
            case "delayed":
                return "DELAYED";
            case "cancelled":
                return "CANCELLED";
            case "landed":
                return "LANDED";
            default:
                return "ON TIME";
        }
    }
}
