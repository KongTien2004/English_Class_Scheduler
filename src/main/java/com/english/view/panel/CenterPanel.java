package com.english.view.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CenterPanel extends JPanel {
    private JTable centerTable;
    private DefaultTableModel tableModel;

    public CenterPanel() {
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
        headerPanel.setBackground(new Color(230, 126, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Center Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Center ID", "Center Name", "Address"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        centerTable = new JTable(tableModel);
        centerTable.setRowHeight(30);
        centerTable.setFont(new Font("Arial", Font.PLAIN, 12));
        centerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        centerTable.getTableHeader().setBackground(new Color(52, 73, 94));
        centerTable.getTableHeader().setForeground(Color.WHITE);
        centerTable.setSelectionBackground(new Color(230, 126, 34));

        JScrollPane scrollPane = new JScrollPane(centerTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Sample data
        tableModel.addRow(new Object[]{"CTR001", "Downtown Center", "123 Main St, City"});
        tableModel.addRow(new Object[]{"CTR002", "Uptown Center", "456 Park Ave, City"});

        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        JButton addButton = createStyledButton("Add Center", new Color(46, 204, 113));
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
