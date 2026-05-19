package com.airline.exception;

/**
 * Thrown when a flight code or scheduled flight ID is not found.
 */
public class FlightNotFoundException extends AirlineException {
    public FlightNotFoundException(String message) {
        super(message);
    }
}
