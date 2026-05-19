package com.airline.main;

import com.airline.AirlineWebApplication;
import com.airline.ui.MainFrame;
import com.formdev.flatlaf.FlatDarkLaf;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class GUIMainApplication {

    public static void main(String[] args) {
        // Setup Look and Feel before starting
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme.");
        }

        // Launch Spring Boot application in non-headless mode for Swing compatibility
        ConfigurableApplicationContext context = new SpringApplicationBuilder(AirlineWebApplication.class)
                .headless(false)
                .run(args);

        // Start MainFrame on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}