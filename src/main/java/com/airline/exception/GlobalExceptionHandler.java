package com.airline.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.airline.util.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        Logger.error("Unhandled system exception: ", ex);
        model.addAttribute("errorTitle", "System Error");
        model.addAttribute("errorMessage", "An unexpected error occurred processing your request.");
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Page Not Found");
        model.addAttribute("errorMessage", "The page you are looking for does not exist or has been moved.");
        return "error";
    }

    @ExceptionHandler({InvalidInputException.class, UserAlreadyExistsException.class, UserNotFoundException.class, FlightNotFoundException.class, SeatsNotAvailableException.class, PaymentFailedException.class, AirlineException.class})
    public String handleBusinessExceptions(Exception ex, Model model) {
        Logger.warn("Business logic exception: " + ex.getMessage());
        model.addAttribute("errorTitle", "Request Error");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}
