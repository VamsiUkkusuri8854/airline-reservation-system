/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.ui;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import com.airline.model.User;
import com.airline.service.FlightService;
import com.airline.service.ReservationService;
import com.airline.ui.custom.GradientPanel;
import com.airline.ui.custom.RoundedButton;
import com.airline.ui.custom.UIHelper;
import com.airline.util.SpringContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerDashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private User currentUser;
    private CardLayout centerCardLayout;
    private JPanel centerCardPanel;

    // Search panel fields
    private JTextField txtSource;
    private JTextField txtDestination;
    private JTable tblFlights;
    private DefaultTableModel modelFlights;
    private List<Flight> flightList;

    // Booking / History panels
    private JTable tblHistory;
    private DefaultTableModel modelHistory;

    public CustomerDashboardPanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.currentUser = user;
        setLayout(new BorderLayout());

        // Sidebar Navigation
        GradientPanel sidebar = new GradientPanel(new Color(10, 37, 64), new Color(15, 60, 100));
        sidebar.setPreferredSize(new Dimension(250, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel lblBrand = new JLabel("✈  FlyHigh Portal", JLabel.CENTER);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblBrand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblWelcome = new JLabel("Welcome, " + user.getName(), JLabel.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblWelcome.setForeground(new Color(200, 230, 255));
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblWelcome);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Nav Buttons
        RoundedButton btnSearch = new RoundedButton("Search & Book", new Color(0, 168, 204, 100),
                new Color(0, 168, 204));
        btnSearch.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSearch.setMaximumSize(new Dimension(210, 40));
        sidebar.add(btnSearch);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        RoundedButton btnHistory = new RoundedButton("My Bookings", new Color(0, 168, 204, 100),
                new Color(0, 168, 204));
        btnHistory.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnHistory.setMaximumSize(new Dimension(210, 40));
        sidebar.add(btnHistory);
        sidebar.add(Box.createRigidArea(new Dimension(0, 150)));

        RoundedButton btnSignOut = new RoundedButton("Sign Out", new Color(220, 80, 80, 150), new Color(220, 50, 50));
        btnSignOut.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSignOut.setMaximumSize(new Dimension(210, 40));
        sidebar.add(btnSignOut);

        add(sidebar, BorderLayout.WEST);

        // Center Content Area
        centerCardLayout = new CardLayout();
        centerCardPanel = new JPanel(centerCardLayout);
        centerCardPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Create Sub-Panels
        JPanel searchPanel = createSearchPanel();
        JPanel historyPanel = createHistoryPanel();

        centerCardPanel.add(searchPanel, "search");
        centerCardPanel.add(historyPanel, "history");

        add(centerCardPanel, BorderLayout.CENTER);

        // Nav Actions
        btnSearch.addActionListener(e -> centerCardLayout.show(centerCardPanel, "search"));
        btnHistory.addActionListener(e -> {
            refreshBookingHistory();
            centerCardLayout.show(centerCardPanel, "history");
        });
        btnSignOut.addActionListener(e -> mainFrame.showLogin());

        // Initial fetch
        refreshFlights();
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));

        // Filters Header
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setBorder(BorderFactory.createTitledBorder("Search Flights"));

        filterBar.add(new JLabel("From:"));
        txtSource = new JTextField(12);
        filterBar.add(txtSource);

        filterBar.add(new JLabel("To:"));
        txtDestination = new JTextField(12);
        filterBar.add(txtDestination);

        RoundedButton btnFind = new RoundedButton("Search", new Color(10, 37, 64), new Color(0, 168, 204));
        btnFind.setPreferredSize(new Dimension(100, 32));
        filterBar.add(btnFind);

        RoundedButton btnClear = new RoundedButton("Reset", new Color(100, 100, 100), new Color(150, 150, 150));
        btnClear.setPreferredSize(new Dimension(90, 32));
        filterBar.add(btnClear);

        panel.add(filterBar, BorderLayout.NORTH);

        // Flights Table
        String[] cols = { "ID", "Flight No", "Source", "Destination", "Departure", "Arrival", "Avail Seats", "Price" };
        modelFlights = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblFlights = new JTable(modelFlights);
        UIHelper.styleTable(tblFlights);
        JScrollPane scroll = new JScrollPane(tblFlights);
        panel.add(scroll, BorderLayout.CENTER);

        // Book Flight Action Footer
        JPanel actionFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionFooter.add(new JLabel("Number of Seats:"));
        SpinnerModel sm = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner spinSeats = new JSpinner(sm);
        spinSeats.setPreferredSize(new Dimension(60, 30));
        actionFooter.add(spinSeats);

        RoundedButton btnBook = new RoundedButton("Proceed to Book", new Color(0, 168, 204), new Color(0, 192, 230));
        btnBook.setPreferredSize(new Dimension(160, 38));
        actionFooter.add(btnBook);

        panel.add(actionFooter, BorderLayout.SOUTH);

        // Actions
        btnFind.addActionListener(e -> {
            String src = txtSource.getText().trim();
            String dest = txtDestination.getText().trim();
            try {
                FlightService fs = SpringContext.getBean(FlightService.class);
                if (src.isEmpty() || dest.isEmpty()) {
                    flightList = fs.getAllFlights();
                } else {
                    flightList = fs.searchFlights(src, dest);
                }
                populateFlightsTable(flightList);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnClear.addActionListener(e -> {
            txtSource.setText("");
            txtDestination.setText("");
            refreshFlights();
        });

        btnBook.addActionListener(e -> {
            int row = tblFlights.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a flight from the list.", "Selection Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int seats = (int) spinSeats.getValue();
            Flight selected = flightList.get(row);
            handleBooking(selected, seats);
        });

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createTitledBorder("My Booking History"));

        String[] cols = { "Reservation ID", "PNR Code", "Flight No", "Route", "Departure", "Seats Booked",
                "Amount Paid", "Status" };
        modelHistory = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblHistory = new JTable(modelHistory);
        UIHelper.styleTable(tblHistory);
        JScrollPane scroll = new JScrollPane(tblHistory);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        RoundedButton btnDownload = new RoundedButton("Download PDF Ticket", new Color(10, 37, 64),
                new Color(0, 168, 204));
        btnDownload.setPreferredSize(new Dimension(200, 38));
        footer.add(btnDownload);
        panel.add(footer, BorderLayout.SOUTH);

        btnDownload.addActionListener(e -> {
            int row = tblHistory.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a booking record.", "Selection Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int resId = (int) tblHistory.getValueAt(row, 0);
            downloadTicketPdf(resId);
        });

        return panel;
    }

    private void refreshFlights() {
        try {
            FlightService fs = SpringContext.getBean(FlightService.class);
            flightList = fs.getAllFlights();
            populateFlightsTable(flightList);
        } catch (Exception e) {
            System.err.println("Error fetching flights: " + e.getMessage());
        }
    }

    private void populateFlightsTable(List<Flight> list) {
        modelFlights.setRowCount(0);
        for (Flight f : list) {
            modelFlights.addRow(new Object[] {
                    f.getId(),
                    f.getFlightNumber(),
                    f.getSource(),
                    f.getDestination(),
                    f.getDepartureTime().toString(),
                    f.getArrivalTime().toString(),
                    f.getAvailableSeats(),
                    "$" + String.format("%.2f", f.getPrice())
            });
        }
    }

    private void refreshBookingHistory() {
        try {
            ReservationService rs = SpringContext.getBean(ReservationService.class);
            List<Reservation> bookings = rs.getBookingHistory(currentUser.getId());
            modelHistory.setRowCount(0);
            for (Reservation r : bookings) {
                modelHistory.addRow(new Object[] {
                        r.getId(),
                        r.getPnr(),
                        r.getFlightNumber(),
                        r.getSource() + " -> " + r.getDestination(),
                        r.getDepartureTime() != null ? r.getDepartureTime().toString() : "N/A",
                        r.getSeatsBooked(),
                        "$" + String.format("%.2f", r.getTotalAmount()),
                        r.getStatus()
                });
            }
        } catch (Exception e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        }
    }

    private void handleBooking(Flight flight, int seats) {
        if (flight.getAvailableSeats() < seats) {
            JOptionPane.showMessageDialog(this, "Not enough seats available on this flight.", "Capacity Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double total = flight.getPrice() * seats;
        int choice = JOptionPane.showConfirmDialog(this,
                "Book " + seats + " seat(s) on Flight " + flight.getFlightNumber() + "?\nTotal Price: $"
                        + String.format("%.2f", total),
                "Confirm Booking", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // Payment method dialog
            String[] methods = { "Credit Card", "Net Banking", "UPI" };
            String displayMethod = (String) JOptionPane.showInputDialog(this, "Select Payment Method:",
                    "Payment Method Selection", JOptionPane.PLAIN_MESSAGE, null, methods, methods[0]);

            if (displayMethod != null) {
                String method = "CARD";
                if ("Net Banking".equals(displayMethod)) {
                    method = "NET_BANKING";
                } else if ("UPI".equals(displayMethod)) {
                    method = "UPI";
                }

                try {
                    ReservationService rs = SpringContext.getBean(ReservationService.class);
                    Reservation res = rs.bookTicket(currentUser.getId(), flight.getId(), seats, method);
                    JOptionPane.showMessageDialog(this, "Booking Successful!\nPNR: " + res.getPnr(), "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshFlights();
                    refreshBookingHistory();
                    centerCardLayout.show(centerCardPanel, "history");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void downloadTicketPdf(int reservationId) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("Ticket_PNR_" + reservationId + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = chooser.getSelectedFile();
            try {
                com.airline.service.PdfGeneratorService pdfGen = SpringContext
                        .getBean(com.airline.service.PdfGeneratorService.class);
                ReservationService resService = SpringContext.getBean(ReservationService.class);
                Reservation res = resService.getReservationById(reservationId);
                byte[] pdfBytes = pdfGen.generateBoardingPassPdf(res);
                java.nio.file.Files.write(file.toPath(), pdfBytes);
                JOptionPane.showMessageDialog(this, "PDF Ticket saved successfully to:\n" + file.getAbsolutePath(),
                        "Download Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating PDF: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}