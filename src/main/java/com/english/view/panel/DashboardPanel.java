package com.english.view.panel;

import com.english.service.*;
import com.english.DAO.*;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    // Services
    private StudentService studentService;
    private MentorService mentorService;
    private AssistantService assistantService;
    private CenterService centerService;
    private RoomService roomService;
    private PackageService packageService;
    private LearningPlanService learningPlanService;
    private LearningSessionService learningSessionService;
//    private MentorAvailabilityService mentorAvailabilityService;
//    private StudentAvailabilityService studentAvailabilityService;
//    private StudentPreferenceService studentPreferenceService;

    // Stat cards
    private JLabel studentCountLabel;
    private JLabel mentorCountLabel;
    private JLabel assistantCountLabel;
    private JLabel centerCountLabel;
    private JLabel roomCountLabel;
    private JLabel packageCountLabel;
    private JLabel planCountLabel;
    private JLabel sessionCountLabel;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Initialize services
        initServices();

        // Initialize components
        initComponents();

        // Load statistics
        refreshStatistics();
    }

    private void initServices() {
        studentService = new StudentService(new StudentDAO());
        mentorService = new MentorService(new MentorDAO());
        assistantService = new AssistantService(new AssistantDAO());
        centerService = new CenterService(new CenterDAO());
        roomService = new RoomService(new RoomDAO());
        packageService = new PackageService(new PackageDAO());
        learningPlanService = new LearningPlanService(new LearningPlanDAO());
        learningSessionService = new LearningSessionService(new LearningSessionDAO());
//        mentorAvailabilityService = new MentorAvailabilityService(new MentorAvailabilityDAO());
//        studentAvailabilityService = new StudentAvailabilityService(new StudentAvailabilityDAO());
//        studentPreferenceService = new StudentPreferenceService(new StudentPreferenceDAO());
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
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        statsPanel.setBackground(Color.WHITE);

        // Create stat cards with labels
        statsPanel.add(createStatCard("Total Students", "0", new Color(52, 152, 219), "studentCountLabel"));
        statsPanel.add(createStatCard("Total Mentors", "0", new Color(46, 204, 113), "mentorCountLabel"));
        statsPanel.add(createStatCard("Total Assistants", "0", new Color(155, 89, 182), "assistantCountLabel"));
        statsPanel.add(createStatCard("Total Centers", "0", new Color(230, 126, 34), "centerCountLabel"));
        statsPanel.add(createStatCard("Total Rooms", "0", new Color(231, 76, 60), "roomCountLabel"));
        statsPanel.add(createStatCard("Total Packages", "0", new Color(26, 188, 156), "packageCountLabel"));
        statsPanel.add(createStatCard("Learning Plans", "0", new Color(52, 73, 94), "planCountLabel"));
        statsPanel.add(createStatCard("Total Sessions", "0", new Color(241, 196, 15), "sessionCountLabel"));

        contentPanel.add(statsPanel, BorderLayout.CENTER);

        // Refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton refreshButton = new JButton("🔄 Refresh Statistics");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setPreferredSize(new Dimension(180, 40));
        refreshButton.setFont(new Font("Arial", Font.BOLD, 13));
        refreshButton.addActionListener(e -> refreshStatistics());

        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(52, 152, 219));
            }
        });

        bottomPanel.add(refreshButton);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String initialValue, Color color, String labelName) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Store reference to label based on name
        switch (labelName) {
            case "studentCountLabel":
                studentCountLabel = valueLabel;
                break;
            case "mentorCountLabel":
                mentorCountLabel = valueLabel;
                break;
            case "assistantCountLabel":
                assistantCountLabel = valueLabel;
                break;
            case "centerCountLabel":
                centerCountLabel = valueLabel;
                break;
            case "roomCountLabel":
                roomCountLabel = valueLabel;
                break;
            case "packageCountLabel":
                packageCountLabel = valueLabel;
                break;
            case "planCountLabel":
                planCountLabel = valueLabel;
                break;
            case "sessionCountLabel":
                sessionCountLabel = valueLabel;
                break;
        }

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

    public void refreshStatistics() {
        // Use SwingWorker to load data in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int studentCount, mentorCount, assistantCount, centerCount;
            private int roomCount, packageCount, planCount, sessionCount;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    studentCount = studentService.totalStudents();
                    mentorCount = mentorService.totalMentors();
                    assistantCount = assistantService.totalAssistants();
                    centerCount = centerService.totalNumberOfCenters();
                    roomCount = roomService.totalRooms();
                    packageCount = packageService.totalPackages();
                    planCount = learningPlanService.totalLearningPlans();
                    sessionCount = learningSessionService.totalLearningSessions();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                // Update UI on EDT
                studentCountLabel.setText(String.valueOf(studentCount));
                mentorCountLabel.setText(String.valueOf(mentorCount));
                assistantCountLabel.setText(String.valueOf(assistantCount));
                centerCountLabel.setText(String.valueOf(centerCount));
                roomCountLabel.setText(String.valueOf(roomCount));
                packageCountLabel.setText(String.valueOf(packageCount));
                planCountLabel.setText(String.valueOf(planCount));
                sessionCountLabel.setText(String.valueOf(sessionCount));
            }
        };

        worker.execute();
    }
}
