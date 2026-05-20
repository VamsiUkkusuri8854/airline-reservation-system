/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.exception;

/**
 * Thrown when attempting to book more seats than are currently available.
 */
public class SeatsNotAvailableException extends AirlineException {
    public SeatsNotAvailableException(String message) {
        super(message);
    }
}
