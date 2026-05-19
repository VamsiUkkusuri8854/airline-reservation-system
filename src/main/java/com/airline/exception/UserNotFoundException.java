package com.airline.exception;

/**
 * Thrown when a requested user profile is not found or authentication fails.
 */
public class UserNotFoundException extends AirlineException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
