/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.ui;

import com.airline.model.User;
import com.airline.service.UserService;
import com.airline.ui.custom.GradientPanel;
import com.airline.ui.custom.RoundedButton;
import com.airline.ui.custom.UIHelper;
import com.airline.util.SpringContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends GradientPanel {
    private MainFrame mainFrame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginPanel(MainFrame mainFrame) {
        super(new Color(10, 37, 64), new Color(0, 168, 204));
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Glassmorphic / clean container panel
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(new Color(255, 255, 255, 20)); // Semi-transparent white
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1, true),
                new EmptyBorder(30, 40, 30, 40)
        ));
        cardPanel.setOpaque(false);

        // Logo
        JLabel lblLogo = new JLabel("✈  FLYHIGH PORTAL", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblLogo);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblSub = new JLabel("Your journey begins here", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSub.setForeground(new Color(220, 240, 255));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblSub);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Username Field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(Color.WHITE);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(lblUser);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        txtUsername = new JTextField(20);
        txtUsername.setMaximumSize(new Dimension(300, 35));
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password Field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(Color.WHITE);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(lblPass);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        txtPassword = new JPasswordField(20);
        txtPassword.setMaximumSize(new Dimension(300, 35));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Sign In Button
        RoundedButton btnSignIn = new RoundedButton("Sign In");
        btnSignIn.setMaximumSize(new Dimension(300, 40));
        btnSignIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(btnSignIn);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Sign Up Link
        JButton btnSignUp = new JButton("No account? Sign Up");
        btnSignUp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSignUp.setForeground(Color.WHITE);
        btnSignUp.setContentAreaFilled(false);
        btnSignUp.setBorderPainted(false);
        btnSignUp.setFocusPainted(false);
        btnSignUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSignUp.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnSignUp);

        // Actions
        btnSignIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showRegister();
            }
        });

        add(cardPanel);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserService userService = SpringContext.getBean(UserService.class);
            User user = userService.loginUser(username, password);
            if (user != null) {
                txtUsername.setText("");
                txtPassword.setText("");
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    mainFrame.showAdminDashboard(user);
                } else {
                    mainFrame.showCustomerDashboard(user);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}