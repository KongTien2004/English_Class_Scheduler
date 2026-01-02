package com.english.view.panel;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(Color.WHITE);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        statsPanel.setBackground(Color.WHITE);

        statsPanel.add(createStatCard("Total Students", "0", new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Total Mentors", "0", new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Active Plans", "0", new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Total Centers", "0", new Color(230, 126, 34)));
        statsPanel.add(createStatCard("Total Sessions", "0", new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Total Packages", "0", new Color(26, 188, 156)));
        statsPanel.add(createStatCard("Progress Records", "0", new Color(52, 73, 94)));
        statsPanel.add(createStatCard("Total Revenue", "$0", new Color(241, 196, 15)));

        contentPanel.add(statsPanel);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(titleLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

//    private JPanel createActionCard(String title, String description, String icon) {
//        JPanel card = new JPanel();
//        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
//        card.setBackground(Color.WHITE);
//        card.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
//                BorderFactory.createEmptyBorder(30, 20, 30, 20)
//        ));
//
//        JLabel iconLabel = new JLabel(icon);
//        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
//        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JLabel titleLabel = new JLabel(title);
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JLabel descLabel = new JLabel(description);
//        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
//        descLabel.setForeground(Color.GRAY);
//        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JButton actionButton = new JButton("Start");
//        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//        actionButton.setBackground(new Color(41, 128, 185));
//        actionButton.setForeground(Color.WHITE);
//        actionButton.setFocusPainted(false);
//        actionButton.setBorderPainted(false);
//        actionButton.setPreferredSize(new Dimension(100, 35));
//
//        card.add(iconLabel);
//        card.add(Box.createRigidArea(new Dimension(0, 15)));
//        card.add(titleLabel);
//        card.add(Box.createRigidArea(new Dimension(0, 10)));
//        card.add(descLabel);
//        card.add(Box.createRigidArea(new Dimension(0, 20)));
//        card.add(actionButton);
//
//        return card;
//    }
}
