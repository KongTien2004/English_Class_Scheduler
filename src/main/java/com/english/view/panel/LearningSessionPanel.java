package com.english.view.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LearningSessionPanel extends JPanel {
    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, cancelButton, completeButton, refreshButton;

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
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Learning Session Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search panel
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
        sessionTable.getTableHeader().setForeground(Color.BLACK);
        sessionTable.setSelectionBackground(new Color(41, 128, 185));
        sessionTable.setSelectionForeground(Color.WHITE);
        sessionTable.setGridColor(new Color(189, 195, 199));

        JScrollPane scrollPane = new JScrollPane(sessionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        addButton = createStyledButton("Schedule", new Color(46, 204, 113));
        editButton = createStyledButton("Edit", new Color(52, 152, 219));
        cancelButton = createStyledButton("Cancel", new Color(231, 76, 60));
        completeButton = createStyledButton("Complete", new Color(26, 188, 156));
        refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));

        addButton.addActionListener(e -> showScheduleDialog());
        editButton.addActionListener(e -> showEditSessionDialog());
        cancelButton.addActionListener(e -> cancelSession());
        completeButton.addActionListener(e -> completeSession());
        refreshButton.addActionListener(e -> refreshTable());

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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void showScheduleDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Schedule New Session", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        JTextField planIdField = new JTextField(20);
        JTextField sessionNumField = new JTextField(20);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"LISTENING", "READING", "WRITING", "SPEAKING"});
        JTextField scheduledTimeField = new JTextField(20);
        JTextField locationField = new JTextField(20);

        // Add labels and fields
        addFormField(formPanel, gbc, 0, "Plan ID:", planIdField);
        addFormField(formPanel, gbc, 1, "Session #:", sessionNumField);
        addFormField(formPanel, gbc, 2, "Type:", typeCombo);
        addFormField(formPanel, gbc, 3, "Scheduled Time:", scheduledTimeField);
        addFormField(formPanel, gbc, 4, "Location:", locationField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelBtn = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            // TODO: Add validation and save logic
            JOptionPane.showMessageDialog(dialog, "Session scheduled successfully!");
            dialog.dispose();
            refreshTable();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void showEditSessionDialog() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a session to edit", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Similar to schedule dialog but pre-filled with selected session data
        JOptionPane.showMessageDialog(this, "Edit functionality - to be implemented");
    }

    private void cancelSession() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a session to cancel", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this session?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Update session status to CANCELLED
            JOptionPane.showMessageDialog(this, "Session cancelled successfully!");
            refreshTable();
        }
    }

    private void completeSession() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a session to complete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark this session as completed?",
                "Confirm Complete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Update session status to COMPLETED
            JOptionPane.showMessageDialog(this, "Session marked as completed!");
            refreshTable();
        }
    }

    private void refreshTable() {
        // TODO: Load data from database
        tableModel.setRowCount(0);
    }
}
