/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.exception;

/**
 * Thrown when registering a user with a username or email that already exists.
 */
public class UserAlreadyExistsException extends AirlineException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
