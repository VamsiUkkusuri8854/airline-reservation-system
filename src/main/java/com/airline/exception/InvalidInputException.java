/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.exception;

/**
 * Thrown when custom validations (emails, dates, prices) fail input integrity checks.
 */
public class InvalidInputException extends AirlineException {
    public InvalidInputException(String message) {
        super(message);
    }
}
