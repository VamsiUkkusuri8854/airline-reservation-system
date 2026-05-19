package com.airline.ui;

import com.airline.model.User;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel container;
    private User currentUser;

    public MainFrame() {
        setTitle("FlyHigh Airlines Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Add screens
        container.add(new LoginPanel(this), "login");
        container.add(new RegisterPanel(this), "register");

        add(container);
        cardLayout.show(container, "login");
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