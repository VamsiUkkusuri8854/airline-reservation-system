/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.ui;

import com.airline.dao.FlightDAO;
import com.airline.dao.impl.FlightDAOImpl;
import com.airline.model.Flight;
import com.airline.model.User;
import com.airline.ui.custom.GradientPanel;
import com.airline.ui.custom.RoundedButton;
import com.airline.ui.custom.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminDashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private User currentUser;
    private JTable tblFlights;
    private DefaultTableModel modelFlights;
    private List<Flight> flightList;
    private final FlightDAO flightDAO;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public AdminDashboardPanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.currentUser = user;
        this.flightDAO = new FlightDAOImpl();
        this.df.setLenient(false);

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

        JLabel lblRole = new JLabel("Admin Dashboard", JLabel.CENTER);
        lblRole.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblRole.setForeground(new Color(0, 168, 204));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblRole);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel lblWelcome = new JLabel("Admin: " + user.getName(), JLabel.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblWelcome.setForeground(new Color(200, 220, 240));
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblWelcome);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Nav Buttons
        RoundedButton btnManageFlights = new RoundedButton("Flight Schedule", new Color(0, 168, 204, 100), new Color(0, 168, 204));
        btnManageFlights.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnManageFlights.setMaximumSize(new Dimension(210, 40));
        sidebar.add(btnManageFlights);
        sidebar.add(Box.createRigidArea(new Dimension(0, 200)));

        RoundedButton btnSignOut = new RoundedButton("Sign Out", new Color(220, 80, 80, 150), new Color(220, 50, 50));
        btnSignOut.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSignOut.setMaximumSize(new Dimension(210, 40));
        sidebar.add(btnSignOut);

        add(sidebar, BorderLayout.WEST);

        // Center Panel (Main Administration Area)
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Analytics Metrics Cards
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsPanel.add(createMetricCard("Total Active Flights", "3 scheduled routes", new Color(0, 168, 204)));
        metricsPanel.add(createMetricCard("Fleet Allocation", "98% occupancy rate", new Color(46, 204, 113)));
        metricsPanel.add(createMetricCard("Admin Authorization", "Level 1 Access Granted", new Color(155, 89, 182)));
        centerPanel.add(metricsPanel, BorderLayout.NORTH);

        // Flights Table
        String[] cols = {"Database ID", "Flight Number", "Source", "Destination", "Departure Time", "Arrival Time", "Total Seats", "Available Seats", "Ticket Price"};
        modelFlights = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblFlights = new JTable(modelFlights);
        UIHelper.styleTable(tblFlights);
        JScrollPane scroll = new JScrollPane(tblFlights);
        centerPanel.add(scroll, BorderLayout.CENTER);

        // Action Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        RoundedButton btnAdd = new RoundedButton("Add Route", new Color(46, 204, 113), new Color(39, 174, 96));
        btnAdd.setPreferredSize(new Dimension(140, 38));
        toolbarPanel.add(btnAdd);

        RoundedButton btnEdit = new RoundedButton("Edit Flight", new Color(0, 168, 204), new Color(0, 192, 230));
        btnEdit.setPreferredSize(new Dimension(140, 38));
        toolbarPanel.add(btnEdit);

        RoundedButton btnDelete = new RoundedButton("Remove Flight", new Color(231, 76, 60), new Color(192, 57, 43));
        btnDelete.setPreferredSize(new Dimension(140, 38));
        toolbarPanel.add(btnDelete);

        centerPanel.add(toolbarPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Toolbar Actions
        btnAdd.addActionListener(e -> handleAddFlight());
        btnEdit.addActionListener(e -> handleEditFlight());
        btnDelete.addActionListener(e -> handleDeleteFlight());
        btnSignOut.addActionListener(e -> mainFrame.showLogin());

        // Load data initially
        refreshFlightTable(-1);
    }

    private JPanel createMetricCard(String title, String val, Color topBorder) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(topBorder);
                g.fillRect(0, 0, getWidth(), 4);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(120, 120, 120));
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVal.setForeground(new Color(40, 40, 40));
        card.add(lblVal, BorderLayout.CENTER);

        return card;
    }

    private void refreshFlightTable(int highlightId) {
        try {
            flightList = flightDAO.getAllFlights();
            modelFlights.setRowCount(0);
            int selectIndex = -1;

            for (int i = 0; i < flightList.size(); i++) {
                Flight f = flightList.get(i);
                modelFlights.addRow(new Object[]{
                    f.getId(),
                    f.getFlightNumber(),
                    f.getSource(),
                    f.getDestination(),
                    df.format(f.getDepartureTime()),
                    df.format(f.getArrivalTime()),
                    f.getTotalSeats(),
                    f.getAvailableSeats(),
                    "$" + String.format("%.2f", f.getPrice())
                });
                if (f.getId() == highlightId) {
                    selectIndex = i;
                }
            }

            // Automatically select and highlight row if requested
            if (selectIndex >= 0) {
                tblFlights.setRowSelectionInterval(selectIndex, selectIndex);
                tblFlights.scrollRectToVisible(tblFlights.getCellRect(selectIndex, 0, true));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error listing flights: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddFlight() {
        // Simple Add flight dialog using standard prompts or dialog
        JTextField txtNum = new JTextField();
        JTextField txtSrc = new JTextField();
        JTextField txtDest = new JTextField();
        JTextField txtDep = new JTextField(df.format(new java.util.Date()));
        JTextField txtArr = new JTextField(df.format(new java.util.Date()));
        JTextField txtTotal = new JTextField("150");
        JTextField txtPrice = new JTextField("250.00");

        Object[] fields = {
            "Flight Number:", txtNum,
            "Source City:", txtSrc,
            "Destination City:", txtDest,
            "Departure Time (yyyy-MM-dd HH:mm:ss):", txtDep,
            "Arrival Time (yyyy-MM-dd HH:mm:ss):", txtArr,
            "Total Seats capacity:", txtTotal,
            "Ticket Price ($):", txtPrice
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New Flight Route", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            // Validate inputs
            String num = txtNum.getText().trim();
            String src = txtSrc.getText().trim();
            String dest = txtDest.getText().trim();
            String depStr = txtDep.getText().trim();
            String arrStr = txtArr.getText().trim();
            String totalStr = txtTotal.getText().trim();
            String priceStr = txtPrice.getText().trim();

            if (num.isEmpty() || src.isEmpty() || dest.isEmpty() || depStr.isEmpty() || arrStr.isEmpty() || totalStr.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All input fields are mandatory.", "Add Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int total = Integer.parseInt(totalStr);
                double price = Double.parseDouble(priceStr);
                if (total <= 0) {
                    JOptionPane.showMessageDialog(this, "Total seats capacity must be at least 1.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Ticket price must be greater than 0.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Timestamp dep = new Timestamp(df.parse(depStr).getTime());
                Timestamp arr = new Timestamp(df.parse(arrStr).getTime());
                if (arr.before(dep)) {
                    JOptionPane.showMessageDialog(this, "Arrival time cannot occur before departure time.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (flightDAO.existsByFlightNumber(num)) {
                    JOptionPane.showMessageDialog(this, "Flight Number '" + num + "' already exists.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Flight f = new Flight(num, src, dest, dep, arr, total, total, price);
                if (flightDAO.addFlight(f)) {
                    JOptionPane.showMessageDialog(this, "Route " + num + " scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshFlightTable(-1);
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use 'yyyy-MM-dd HH:mm:ss'.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Seats must be an integer, and price must be a numeric value.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database failure: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEditFlight() {
        int selectedRow = tblFlights.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 8. Ensure JTable selected row data maps correctly to edit form
        int flightId = (int) tblFlights.getValueAt(selectedRow, 0);
        String currentFlightNumber = (String) tblFlights.getValueAt(selectedRow, 1);
        String currentSource = (String) tblFlights.getValueAt(selectedRow, 2);
        String currentDestination = (String) tblFlights.getValueAt(selectedRow, 3);
        String currentDeparture = (String) tblFlights.getValueAt(selectedRow, 4);
        String currentArrival = (String) tblFlights.getValueAt(selectedRow, 5);
        int currentTotalSeats = (int) tblFlights.getValueAt(selectedRow, 6);
        int currentAvailableSeats = (int) tblFlights.getValueAt(selectedRow, 7);
        String currentPriceStr = ((String) tblFlights.getValueAt(selectedRow, 8)).replace("$", "");
        double currentPrice = Double.parseDouble(currentPriceStr);

        // 10. Improve UI styling for edit dialog/window
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modify Flight - " + currentFlightNumber, true);
        editDialog.setMinimumSize(new Dimension(500, 480));
        editDialog.setLocationRelativeTo(this);
        editDialog.setResizable(false);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Fields Setup
        JTextField txtFlightNumber = new JTextField(currentFlightNumber);
        JTextField txtSource = new JTextField(currentSource);
        JTextField txtDestination = new JTextField(currentDestination);
        JTextField txtDeparture = new JTextField(currentDeparture);
        JTextField txtArrival = new JTextField(currentArrival);
        JTextField txtTotalSeats = new JTextField(String.valueOf(currentTotalSeats));
        JTextField txtAvailableSeats = new JTextField(String.valueOf(currentAvailableSeats));
        JTextField txtPrice = new JTextField(String.format("%.2f", currentPrice));

        // Formatting layouts
        int r = 0;
        addFormRow(contentPanel, gbc, "Flight Number:", txtFlightNumber, r++);
        addFormRow(contentPanel, gbc, "Source:", txtSource, r++);
        addFormRow(contentPanel, gbc, "Destination:", txtDestination, r++);
        addFormRow(contentPanel, gbc, "Departure Time (yyyy-MM-dd HH:mm:ss):", txtDeparture, r++);
        addFormRow(contentPanel, gbc, "Arrival Time (yyyy-MM-dd HH:mm:ss):", txtArrival, r++);
        addFormRow(contentPanel, gbc, "Total Capacity:", txtTotalSeats, r++);
        addFormRow(contentPanel, gbc, "Available Seats:", txtAvailableSeats, r++);
        addFormRow(contentPanel, gbc, "Ticket Price ($):", txtPrice, r++);

        // Save & Cancel buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        RoundedButton btnCancel = new RoundedButton("Cancel", new Color(150, 150, 150), new Color(180, 180, 180));
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnPanel.add(btnCancel);

        RoundedButton btnSave = new RoundedButton("Save Changes", new Color(46, 204, 113), new Color(52, 152, 219));
        btnSave.setPreferredSize(new Dimension(150, 35));
        btnPanel.add(btnSave);

        gbc.gridx = 0;
        gbc.gridy = r;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 0, 8);
        contentPanel.add(btnPanel, gbc);

        editDialog.add(contentPanel);

        // Actions
        btnCancel.addActionListener(e -> editDialog.dispose());

        btnSave.addActionListener(e -> {
            String flightNumber = txtFlightNumber.getText().trim();
            String source = txtSource.getText().trim();
            String destination = txtDestination.getText().trim();
            String departureStr = txtDeparture.getText().trim();
            String arrivalStr = txtArrival.getText().trim();
            String totalStr = txtTotalSeats.getText().trim();
            String availStr = txtAvailableSeats.getText().trim();
            String priceStr = txtPrice.getText().trim();

            // 5. Add proper validation
            // Empty fields not allowed
            if (flightNumber.isEmpty() || source.isEmpty() || destination.isEmpty() ||
                departureStr.isEmpty() || arrivalStr.isEmpty() || totalStr.isEmpty() ||
                availStr.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(editDialog, "All fields are mandatory to update the flight schedule.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Seats cannot be negative
                int totalSeats = Integer.parseInt(totalStr);
                int availSeats = Integer.parseInt(availStr);
                if (totalSeats <= 0 || availSeats < 0) {
                    JOptionPane.showMessageDialog(editDialog, "Total seat capacity must be at least 1, and available seats cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (availSeats > totalSeats) {
                    JOptionPane.showMessageDialog(editDialog, "Available seats cannot exceed the total flight capacity limit.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Price must be numeric & positive
                double price = Double.parseDouble(priceStr);
                if (price <= 0.0) {
                    JOptionPane.showMessageDialog(editDialog, "Ticket price must be a positive numeric value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Date parse verification
                Timestamp dep = new Timestamp(df.parse(departureStr).getTime());
                Timestamp arr = new Timestamp(df.parse(arrivalStr).getTime());
                if (arr.before(dep)) {
                    JOptionPane.showMessageDialog(editDialog, "Scheduled flight arrival date cannot occur before departure date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Prevent duplicate flight numbers
                if (flightDAO.existsByFlightNumberExceptId(flightNumber, flightId)) {
                    JOptionPane.showMessageDialog(editDialog, "Another flight with number '" + flightNumber + "' is already scheduled.", "Duplicate Number Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Confirmation Dialog
                int confirm = JOptionPane.showConfirmDialog(editDialog,
                        "Are you sure you want to update Flight " + currentFlightNumber + " with these new details?",
                        "Confirm Changes", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                // 6. Use PreparedStatement for UPDATE query (within FlightDAOImpl.updateFlight)
                Flight updatedFlight = new Flight(flightId, flightNumber, source, destination, dep, arr, totalSeats, availSeats, price);
                boolean success = flightDAO.updateFlight(updatedFlight);

                if (success) {
                    editDialog.dispose();
                    // JOptionPane Success popup
                    JOptionPane.showMessageDialog(this, "Flight " + flightNumber + " schedule details successfully updated!", "Update Complete", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh JTable automatically
                    refreshFlightTable(flightId);
                } else {
                    JOptionPane.showMessageDialog(editDialog, "No database rows updated. Make sure flight ID is valid.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(editDialog, "Invalid datetime format. Expected 'yyyy-MM-dd HH:mm:ss'.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, "Format mismatch. Capacities/seats must be integer values, price must be a numeric value.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                // 9. Handle exceptions properly
                JOptionPane.showMessageDialog(editDialog, "Database failure: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editDialog.setVisible(true);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, JTextField tf, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(tf, gbc);
    }

    private void handleDeleteFlight() {
        int selectedRow = tblFlights.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightId = (int) tblFlights.getValueAt(selectedRow, 0);
        String flNo = (String) tblFlights.getValueAt(selectedRow, 1);

        int choice = JOptionPane.showConfirmDialog(this,
                "Cancel and remove flight route " + flNo + " permanently from system schedules?\n(This will delete all linked reservations)",
                "Confirm Flight Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (flightDAO.deleteFlight(flightId)) {
                    JOptionPane.showMessageDialog(this, "Flight schedule " + flNo + " deleted successfully.", "Removal Complete", JOptionPane.INFORMATION_MESSAGE);
                    refreshFlightTable(-1);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database failure: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}