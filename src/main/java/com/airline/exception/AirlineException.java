package com.airline.exception;

/**
 * Base exception class for the Airline Reservation System.
 * All domain-specific custom exceptions inherit from this class.
 */
public class AirlineException extends RuntimeException {
    public AirlineException(String message) {
        super(message);
    }

    public AirlineException(String message, Throwable cause) {
        super(message, cause);
    }
}
