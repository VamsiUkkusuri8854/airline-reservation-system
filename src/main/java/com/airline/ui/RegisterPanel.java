package com.airline.ui;

import com.airline.service.UserService;
import com.airline.ui.custom.GradientPanel;
import com.airline.ui.custom.RoundedButton;
import com.airline.util.SpringContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPanel extends GradientPanel {
    private MainFrame mainFrame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;

    public RegisterPanel(MainFrame mainFrame) {
        super(new Color(10, 37, 64), new Color(0, 168, 204));
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(new Color(255, 255, 255, 20));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1, true),
                new EmptyBorder(25, 40, 25, 40)
        ));
        cardPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("✈  CREATE ACCOUNT", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblLogo);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Form Fields Helper
        cardPanel.add(createFieldLabel("Full Name"));
        txtName = new JTextField(20);
        txtName.setMaximumSize(new Dimension(300, 32));
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtName);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        cardPanel.add(createFieldLabel("Username"));
        txtUsername = new JTextField(20);
        txtUsername.setMaximumSize(new Dimension(300, 32));
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        cardPanel.add(createFieldLabel("Password"));
        txtPassword = new JPasswordField(20);
        txtPassword.setMaximumSize(new Dimension(300, 32));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        cardPanel.add(createFieldLabel("Email Address"));
        txtEmail = new JTextField(20);
        txtEmail.setMaximumSize(new Dimension(300, 32));
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtEmail);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        cardPanel.add(createFieldLabel("Phone Number"));
        txtPhone = new JTextField(20);
        txtPhone.setMaximumSize(new Dimension(300, 32));
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPhone.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtPhone);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        // Buttons
        RoundedButton btnSignUp = new RoundedButton("Register");
        btnSignUp.setMaximumSize(new Dimension(300, 38));
        btnSignUp.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(btnSignUp);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnBack = new JButton("Already have an account? Sign In");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setForeground(Color.WHITE);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnBack);

        // Listeners
        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showLogin();
            }
        });

        add(cardPanel);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(230, 245, 255));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void handleRegister() {
        String name = txtName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserService userService = SpringContext.getBean(UserService.class);
            userService.registerUser(username, password, name, email, phone, "CUSTOMER");
            JOptionPane.showMessageDialog(this, "Account created successfully! Please sign in.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showLogin();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}