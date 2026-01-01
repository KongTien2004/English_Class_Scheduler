package com.english.view;

import com.english.view.panel.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Panels
    private DashboardPanel dashboardPanel;
    private StudentPanel studentPanel;
    private MentorPanel mentorPanel;
    private AssistantPanel assistantPanel;
    private RoomPanel roomPanel;
    private CenterPanel centerPanel;
    private PackagePanel packagePanel;
    private LearningPlanPanel learningPlanPanel;
    private LearningSessionPanel learningSessionPanel;
    private MentorAvailabilityPanel mentorAvailabilityPanel;
    private StudentAvailabilityPanel studentAvailabilityPanel;
    private StudentPreferencePanel studentPreferencePanel;

    public MainFrame() {
        setTitle("IELTS Class Scheduler");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Main layout
        setLayout(new BorderLayout());

        // Menu bar
        setJMenuBar(createMenuBar());

        // Side menu
        add(createSideMenu(), BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Initialize panels
        dashboardPanel = new DashboardPanel();
        studentPanel = new StudentPanel();
        mentorPanel = new MentorPanel();
        assistantPanel = new AssistantPanel();
        roomPanel = new RoomPanel();
        centerPanel = new CenterPanel();
        packagePanel = new PackagePanel();
        learningPlanPanel = new LearningPlanPanel();
        learningSessionPanel = new LearningSessionPanel();
        mentorAvailabilityPanel = new MentorAvailabilityPanel();
        studentAvailabilityPanel = new StudentAvailabilityPanel();
        studentPreferencePanel = new StudentPreferencePanel();

        // Add panels to card layout
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(studentPanel, "Students");
        contentPanel.add(mentorPanel, "Mentors");
        contentPanel.add(assistantPanel, "Assistants");
        contentPanel.add(roomPanel, "Rooms");
        contentPanel.add(centerPanel, "Centers");
        contentPanel.add(packagePanel, "Packages");
        contentPanel.add(learningPlanPanel, "Learning Plans");
        contentPanel.add(learningSessionPanel, "Learning Sessions");
        contentPanel.add(mentorAvailabilityPanel, "Mentor Avalabilities");
        contentPanel.add(studentAvailabilityPanel, "Student Availabilities");
        contentPanel.add(studentPreferencePanel, "Student Preferences");

        add(contentPanel, BorderLayout.CENTER);

        // Show dashboard by default
        showPanel("Dashboard");
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(41, 128, 185));

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Management menu
        JMenu managementMenu = new JMenu("Management");
        managementMenu.setForeground(Color.WHITE);

        String[] menuItems = {"Students", "Mentors", "Centers", "Packages",
                "Learning Plans", "Learning Sessions", "Progress Records", "Purchases"};
        for (String item : menuItems) {
            JMenuItem menuItem = new JMenuItem(item);
            menuItem.addActionListener(e -> showPanel(item));
            managementMenu.add(menuItem);
        }
        menuBar.add(managementMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createSideMenu() {
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBackground(new Color(52, 73, 94));
        sideMenu.setPreferredSize(new Dimension(250, getHeight()));

        // Logo/Title
        JLabel titleLabel = new JLabel("IELTS Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sideMenu.add(titleLabel);

        sideMenu.add(Box.createRigidArea(new Dimension(0, 10)));

        // Menu buttons
        String[] menuItems = {
                "Dashboard", "Students", "Mentors", "Centers", "Packages",
                "Learning Plans", "Learning Sessions", "Progress Records", "Purchases"
        };

//        String[] icons = {
//                "📊", "👨‍🎓", "👨‍🏫", "🏢", "📦",
//                "📋", "📅", "📈", "💰"
//        };

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createMenuButton(menuItems[i], menuItems[i]);
            sideMenu.add(menuButton);
            sideMenu.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Add glue to push everything to the top
        sideMenu.add(Box.createVerticalGlue());

        return sideMenu;
    }

    private JButton createMenuButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(230, 45));
        button.setPreferredSize(new Dimension(230, 45));
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }
        });

        button.addActionListener(e -> showPanel(panelName));

        return button;
    }

    private void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "IELTS Learning Center Management System\n" +
                        "Version 1.0\n" +
                        "Developed for managing IELTS learning centers,\n" +
                        "students, mentors, and learning progress.",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame();
        });
    }
}
