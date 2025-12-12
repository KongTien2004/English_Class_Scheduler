package com.english.view.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LearningSessionPanel extends JPanel {
    private JTable sessionTable;
    private DefaultTableModel tableModel;

    public LearningSessionPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        add(createHeader(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(231, 76, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Learning Session Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {
                "Session ID", "Plan ID", "Session #", "Type", "Scheduled Time",
                "Actual Start", "Actual End", "Location", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sessionTable = new JTable(tableModel);
        sessionTable.setRowHeight(30);
        sessionTable.setFont(new Font("Arial", Font.PLAIN, 12));
        sessionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        sessionTable.getTableHeader().setBackground(new Color(52, 73, 94));
        sessionTable.getTableHeader().setForeground(Color.WHITE);
        sessionTable.setSelectionBackground(new Color(231, 76, 60));

        JScrollPane scrollPane = new JScrollPane(sessionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Sample data
        tableModel.addRow(new Object[]{
                "SES001", "PLN001", "1", "ONLINE", "2024-01-20 10:00",
                "2024-01-20 10:05", "2024-01-20 11:50", "Zoom Meeting", "COMPLETED"
        });

        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        JButton addButton = createStyledButton("Schedule", new Color(46, 204, 113));
        JButton editButton = createStyledButton("Edit", new Color(52, 152, 219));
        JButton cancelButton = createStyledButton("Cancel", new Color(231, 76, 60));
        JButton completeButton = createStyledButton("Complete", new Color(26, 188, 156));
        JButton refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));

        buttonPanel.add(refreshButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }
}
