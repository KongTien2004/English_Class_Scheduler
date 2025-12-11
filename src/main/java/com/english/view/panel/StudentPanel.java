package com.english.view.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentPanel extends JPanel {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;

    public StudentPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);

        // Button panel
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Student Management");
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
                "Student ID", "Name", "Phone", "Email", "IELTS Type",
                "Target Band", "Listening", "Reading", "Writing", "Speaking", "Center ID"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(new Color(52, 73, 94));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.setSelectionBackground(new Color(41, 128, 185));
        studentTable.setSelectionForeground(Color.WHITE);
        studentTable.setGridColor(new Color(189, 195, 199));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        addButton = createStyledButton("Add Student", new Color(46, 204, 113));
        editButton = createStyledButton("Edit", new Color(52, 152, 219));
        deleteButton = createStyledButton("Delete", new Color(231, 76, 60));
        refreshButton = createStyledButton("Refresh", new Color(149, 165, 166));

        addButton.addActionListener(e -> showAddStudentDialog());
        editButton.addActionListener(e -> showEditStudentDialog());
        deleteButton.addActionListener(e -> deleteStudent());
        refreshButton.addActionListener(e -> refreshTable());

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

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Student", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        JTextField idField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JComboBox<String> ieltsTypeCombo = new JComboBox<>(new String[]{"GENERAL", "ACADEMIC"});
        JTextField targetBandField = new JTextField(20);
        JTextField listeningField = new JTextField(20);
        JTextField readingField = new JTextField(20);
        JTextField writingField = new JTextField(20);
        JTextField speakingField = new JTextField(20);
        JTextField centerIdField = new JTextField(20);

        // Add labels and fields
        addFormField(formPanel, gbc, 0, "Student ID:", idField);
        addFormField(formPanel, gbc, 1, "Name:", nameField);
        addFormField(formPanel, gbc, 2, "Phone:", phoneField);
        addFormField(formPanel, gbc, 3, "Email:", emailField);
        addFormField(formPanel, gbc, 4, "IELTS Type:", ieltsTypeCombo);
        addFormField(formPanel, gbc, 5, "Target Band:", targetBandField);
        addFormField(formPanel, gbc, 6, "Listening Band:", listeningField);
        addFormField(formPanel, gbc, 7, "Reading Band:", readingField);
        addFormField(formPanel, gbc, 8, "Writing Band:", writingField);
        addFormField(formPanel, gbc, 9, "Speaking Band:", speakingField);
        addFormField(formPanel, gbc, 10, "Center ID:", centerIdField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            // TODO: Add validation and save logic
            JOptionPane.showMessageDialog(dialog, "Student added successfully!");
            dialog.dispose();
            refreshTable();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

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

    private void showEditStudentDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Similar to add dialog but pre-filled with selected student data
        JOptionPane.showMessageDialog(this, "Edit functionality - to be implemented");
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Student deleted successfully!");
        }
    }

    private void refreshTable() {
        // TODO: Load data from database
        tableModel.setRowCount(0);
        // Sample data
        tableModel.addRow(new Object[]{"STD001", "John Doe", "0123456789", "john@email.com",
                "ACADEMIC", "7.0", "6.5", "7.0", "6.0", "7.5", "CTR001"});
    }
}
