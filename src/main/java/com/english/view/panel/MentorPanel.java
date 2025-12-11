package com.english.view.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MentorPanel extends JPanel {
    private JTable mentorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public MentorPanel() {
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
        headerPanel.setBackground(new Color(46, 204, 113));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Mentor Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        searchPanel.add(searchLabel);
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("🔍");
        searchButton.setFocusPainted(false);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {
                "Mentor ID", "Name", "Email", "Certified Band",
                "Can Teach General", "Can Teach Academic", "Available"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 4 && columnIndex <= 6) return Boolean.class;
                return String.class;
            }
        };

        mentorTable = new JTable(tableModel);
        mentorTable.setRowHeight(30);
        mentorTable.setFont(new Font("Arial", Font.PLAIN, 12));
        mentorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        mentorTable.getTableHeader().setBackground(new Color(52, 73, 94));
        mentorTable.getTableHeader().setForeground(Color.WHITE);
        mentorTable.setSelectionBackground(new Color(46, 204, 113));
        mentorTable.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mentorTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Sample data
        tableModel.addRow(new Object[]{"MNT001", "Dr. Smith", "smith@email.com", "8.5", true, true, true});
        tableModel.addRow(new Object[]{"MNT002", "Ms. Johnson", "johnson@email.com", "8.0", true, false, true});

        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        JButton addButton = createStyledButton("Add Mentor", new Color(46, 204, 113));
        JButton editButton = createStyledButton("Edit", new Color(52, 152, 219));
        JButton deleteButton = createStyledButton("Delete", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

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
