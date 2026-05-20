package com.airline.ui;

import com.airline.model.User;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.Font;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel container;
    private User currentUser;

    public MainFrame() {
        setTitle("FlyHigh Airlines Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Add Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About / Version");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Add screens
        container.add(new LoginPanel(this), "login");
        container.add(new RegisterPanel(this), "register");

        add(container);
        cardLayout.show(container, "login");
    }

    private void showAboutDialog() {
        String aboutText = "<html><body style='width: 300px; padding: 10px; font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #00a8cc; margin-top: 0;'>FlyHigh Airlines System</h2>"
                + "<hr>"
                + "<p><b>Developer:</b> Vamsi Ukkusuri</p>"
                + "<p><b>Project Name:</b> Airline Reservation System</p>"
                + "<p><b>Version:</b> 1.0</p>"
                + "<p><b>Year:</b> 2026</p>"
                + "<br>"
                + "<p style='font-size: 10px; color: #7f8c8d; text-align: center;'>"
                + "&copy; 2026 Vamsi Ukkusuri. All Rights Reserved.</p>"
                + "</body></html>";
        JOptionPane.showMessageDialog(this, aboutText, "About FlyHigh Airlines", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showLogin() {
        currentUser = null;
        cardLayout.show(container, "login");
    }

    public void showRegister() {
        cardLayout.show(container, "register");
    }

    public void showCustomerDashboard(User user) {
        this.currentUser = user;
        // Dynamically create dashboards to load latest data on login
        CustomerDashboardPanel customerPanel = new CustomerDashboardPanel(this, user);
        container.add(customerPanel, "customer");
        cardLayout.show(container, "customer");
    }

    public void showAdminDashboard(User user) {
        this.currentUser = user;
        AdminDashboardPanel adminPanel = new AdminDashboardPanel(this, user);
        container.add(adminPanel, "admin");
        cardLayout.show(container, "admin");
    }

    public User getCurrentUser() {
        return currentUser;
    }
}