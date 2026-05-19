package com.airline.service;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import com.airline.model.User;
import com.airline.util.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Service simulating real-world email notifications by generating beautifully styled 
 * portfolio-level HTML email templates and saving them as files to local directories.
 */
@Service
public class EmailService {

    private static final String EMAILS_DIR = "C:\\Users\\ACER\\.gemini\\antigravity\\brain\\bd366cff-f9ee-4235-8b7f-72eef9e0707b\\emails";

    public EmailService() {
        try {
            File dir = new File(EMAILS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            Logger.error("Failed to initialize simulated emails directory: " + e.getMessage());
        }
    }

    /**
     * Dispatches a booking confirmation email.
     */
    public void sendBookingConfirmation(Reservation reservation, User user, Flight flight) {
        String filename = "booking_confirm_" + reservation.getPnr() + ".html";
        String htmlContent = buildEmailTemplate(
            "Booking Confirmation - FlyHigh Airlines",
            "Dear " + user.getName() + ",",
            "Thank you for choosing FlyHigh Airlines! Your reservation is confirmed. Below are your boarding details.",
            "CONFIRMED",
            "PNR Code: " + reservation.getPnr(),
            "Route: " + flight.getSource() + " ➔ " + flight.getDestination(),
            "Departure: " + flight.getDepartureTime(),
            "Assigned Seats: " + (reservation.getSeats() != null && !reservation.getSeats().isEmpty() ? reservation.getSeats() : "Auto-Assigned"),
            "Total Charge: $" + reservation.getTotalAmount()
        );
        writeEmailToFile(filename, htmlContent);
    }

    /**
     * Dispatches a booking cancellation email.
     */
    public void sendBookingCancellation(Reservation reservation, User user, Flight flight) {
        String filename = "booking_cancel_" + reservation.getPnr() + ".html";
        String htmlContent = buildEmailTemplate(
            "Booking Cancelled - FlyHigh Airlines",
            "Dear " + user.getName() + ",",
            "We have processed your request to cancel your booking. Your transaction has been voided.",
            "CANCELLED",
            "PNR Code: " + reservation.getPnr(),
            "Route: " + flight.getSource() + " ➔ " + flight.getDestination(),
            "Departure: " + flight.getDepartureTime(),
            "Seats Voided: " + (reservation.getSeats() != null ? reservation.getSeats() : "N/A"),
            "Refund Settled: $" + reservation.getTotalAmount()
        );
        writeEmailToFile(filename, htmlContent);
    }

    /**
     * Dispatches a flight status change notification.
     */
    public void sendFlightStatusUpdate(User user, Flight flight, String oldStatus, String newStatus) {
        String filename = "flight_status_" + flight.getFlightNumber() + "_" + System.currentTimeMillis() + ".html";
        String htmlContent = buildEmailTemplate(
            "FLIGHT STATUS CHANGE: " + flight.getFlightNumber(),
            "Dear Passenger " + user.getName() + ",",
            "Please be advised that your upcoming scheduled flight status has been modified.",
            newStatus,
            "Flight Number: " + flight.getFlightNumber(),
            "Route: " + flight.getSource() + " ➔ " + flight.getDestination(),
            "Scheduled Departure: " + flight.getDepartureTime(),
            "Previous Status: " + oldStatus,
            "Current Status: " + newStatus
        );
        writeEmailToFile(filename, htmlContent);
    }

    private String buildEmailTemplate(String title, String salutation, String bodyText, String badge, String... details) {
        StringBuilder detailsHtml = new StringBuilder();
        for (String detail : details) {
            detailsHtml.append("<div style='margin-bottom: 8px; font-size: 14px; color: #4a5568;'>")
                       .append("<strong>").append(detail.split(":")[0]).append(":</strong>")
                       .append(detail.contains(":") ? detail.substring(detail.indexOf(":") + 1) : "")
                       .append("</div>");
        }

        String badgeBg = "#00a8cc";
        if ("CANCELLED".equals(badge) || "CANCELLED".equals(badge)) {
            badgeBg = "#e53e3e";
        } else if ("DELAYED".equals(badge)) {
            badgeBg = "#dd6b20";
        } else if ("CONFIRMED".equals(badge) || "ON TIME".equals(badge)) {
            badgeBg = "#38a169";
        }

        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "  <meta charset='utf-8'>" +
               "  <title>" + title + "</title>" +
               "</head>" +
               "<body style=\"font-family: 'Segoe UI', Arial, sans-serif; background-color: #f7fafc; margin: 0; padding: 40px;\">" +
               "  <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid #e2e8f0;'>" +
               "    <!-- Header -->" +
               "    <div style='background-color: #0a2540; padding: 30px; text-align: center;'>" +
               "      <h2 style='color: #ffffff; margin: 0; font-weight: 600; letter-spacing: 1px;'>✈ FlyHigh Airlines</h2>" +
               "      <span style='color: #00a8cc; font-size: 12px; text-transform: uppercase; font-weight: bold;'>Simulated Notification Center</span>" +
               "    </div>" +
               "    <!-- Content -->" +
               "    <div style='padding: 35px;'>" +
               "      <div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px;'>" +
               "        <span style='font-size: 18px; font-weight: bold; color: #1a202c;'>" + title + "</span>" +
               "        <span style='background-color: " + badgeBg + "; color: white; padding: 4px 10px; border-radius: 9999px; font-size: 11px; font-weight: bold; text-transform: uppercase;'>" + badge + "</span>" +
               "      </div>" +
               "      <p style='font-size: 15px; color: #2d3748; line-height: 1.6; margin-bottom: 20px;'>" + salutation + "</p>" +
               "      <p style='font-size: 15px; color: #4a5568; line-height: 1.6; margin-bottom: 30px;'>" + bodyText + "</p>" +
               "      <!-- Details Card -->" +
               "      <div style='background-color: #f8fafc; border-left: 4px solid #00a8cc; padding: 20px; border-radius: 0 8px 8px 0; margin-bottom: 30px;'>" +
               "        " + detailsHtml.toString() + "" +
               "      </div>" +
               "      <p style='font-size: 13px; color: #718096; line-height: 1.5;'>This is a simulated email sent dynamically by FlyHigh's core notification dispatch engine. In a production cloud environment, this triggers a Sengrid or AWS SES SMTP transfer relay.</p>" +
               "    </div>" +
               "    <!-- Footer -->" +
               "    <div style='background-color: #edf2f7; padding: 20px; text-align: center; border-top: 1px solid #e2e8f0;'>" +
               "      <span style='font-size: 12px; color: #718096;'>© 2026 FlyHigh Aviation Group. All Rights Reserved.</span>" +
               "    </div>" +
               "  </div>" +
               "</body>" +
               "</html>";
    }

    private void writeEmailToFile(String filename, String content) {
        try {
            File targetFile = new File(EMAILS_DIR, filename);
            try (FileWriter writer = new FileWriter(targetFile)) {
                writer.write(content);
            }
            Logger.info("Simulated email saved successfully to: " + targetFile.getAbsolutePath());
        } catch (IOException e) {
            Logger.error("Failed to write simulated email file: " + e.getMessage());
        }
    }
}
