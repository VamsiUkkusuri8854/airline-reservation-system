package com.airline.exception;

/**
 * Thrown when booking transactions fail during payment gateway processing.
 */
public class PaymentFailedException extends AirlineException {
    public PaymentFailedException(String message) {
        super(message);
    }
}
