package com.airline.ui.custom;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.InputStream;

public class UIHelper {

    public static ImageIcon getScaledIcon(String resourcePath, int width, int height) {
        try {
            InputStream input = UIHelper.class.getClassLoader().getResourceAsStream(resourcePath);
            if (input == null) {
                return null;
            }
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            ImageIcon originalIcon = new ImageIcon(buffer);
            Image img = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("Error loading image: " + resourcePath + " " + e.getMessage());
            return null;
        }
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(0, 168, 204, 150));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(10, 37, 64));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}