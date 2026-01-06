package com.english.view.panel;

import com.english.model.*;
import com.english.model.Package;
import com.english.service.*;
import com.english.DAO.*;
import com.english.controller.*;
import com.english.algorithm.Scheduler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SchedulerPanel extends JPanel {
    // Services
    private StudentService studentService;
    private StudentAvailabilityService studentAvailabilityService;
    private StudentPreferenceService studentPreferenceService;
    private LearningPlanService learningPlanService;
    private LearningSessionService learningSessionService;
    private RoomService roomService;
    private CenterService centerService;
    private PackageService packageService;

    // Scheduler
    private Scheduler scheduler;

    // Input components
    private JComboBox<Student> studentComboBox;
    private JTextArea availabilityTextArea;
    private JTextArea preferenceTextArea;
    private JComboBox<LearningPlan> planComboBox;

    // Output components
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel summaryLabel;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public SchedulerPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initServices();
        initScheduler();
        initComponents();
        loadStudents();
    }

    private void initServices() {
        studentService = new StudentService(new StudentDAO());
        studentAvailabilityService = new StudentAvailabilityService(new StudentAvailabilityDAO());
        studentPreferenceService = new StudentPreferenceService(new StudentPreferenceDAO());
        learningPlanService = new LearningPlanService(new LearningPlanDAO());
        learningSessionService = new LearningSessionService(new LearningSessionDAO());
        roomService = new RoomService(new RoomDAO());
        centerService = new CenterService(new CenterDAO());
        packageService = new PackageService(new PackageDAO());
    }

    private void initScheduler() {
        // Initialize all controllers needed for Scheduler
        StudentController studentController = new StudentController(new StudentService(new StudentDAO()));
        MentorController mentorController = new MentorController(new MentorService(new MentorDAO()));
        AssistantController assistantController = new AssistantController(new AssistantService(new AssistantDAO()));
        CenterController centerController = new CenterController(new CenterService(new CenterDAO()));
        RoomController roomController = new RoomController(new RoomService(new RoomDAO()));
        LearningPlanController learningPlanController = new LearningPlanController(new LearningPlanService(new LearningPlanDAO()));
        LearningSessionController learningSessionController = new LearningSessionController(new LearningSessionService(new LearningSessionDAO()));
        MentorAvailabilityController mentorAvailabilityController = new MentorAvailabilityController(new MentorAvailabilityService(new MentorAvailabilityDAO()));
        PackageController packageController = new PackageController(new PackageService(new PackageDAO()));
        StudentAvailabilityController studentAvailabilityController = new StudentAvailabilityController(new StudentAvailabilityService(new StudentAvailabilityDAO()));
        StudentPreferenceController studentPreferenceController = new StudentPreferenceController(new StudentPreferenceService(new StudentPreferenceDAO()));

        scheduler = new Scheduler(
                studentController,
                mentorController,
                assistantController,
                centerController,
                roomController,
                learningPlanController,
                learningSessionController,
                mentorAvailabilityController,
                packageController,
                studentAvailabilityController,
                studentPreferenceController
        );
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);

        // Main content - split into input and output
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        // Input panel (top)
        JPanel inputPanel = createInputPanel();
        splitPane.setTopComponent(inputPanel);

        // Output panel (bottom)
        JPanel outputPanel = createOutputPanel();
        splitPane.setBottomComponent(outputPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(142, 68, 173));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("📅 Schedule Generator - Hill Climbing Algorithm");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 10, 20),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(142, 68, 173), 2),
                        "Input - Student Information",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(142, 68, 173)
                )
        ));

        // Form panel with grid layout
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Student Selection
        JPanel studentPanel = createFormSection("Select Student:", createStudentSelector());
        formPanel.add(studentPanel);

        // Learning Plan Selection
        JPanel planPanel = createFormSection("Select Learning Plan:", createPlanSelector());
        formPanel.add(planPanel);

        // Student Availability
        JPanel availabilityPanel = createFormSection("Student Availability:", createAvailabilityDisplay());
        formPanel.add(availabilityPanel);

        // Student Preference
        JPanel preferencePanel = createFormSection("Student Preferences:", createPreferenceDisplay());
        formPanel.add(preferencePanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshButton = createStyledButton("🔄 Refresh Data", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> refreshStudentData());

        JButton generateButton = createStyledButton("⚡ Generate Schedule", new Color(46, 204, 113));
        generateButton.addActionListener(e -> generateSchedule());

        JButton clearButton = createStyledButton("🗑️ Clear Results", new Color(231, 76, 60));
        clearButton.addActionListener(e -> clearResults());

        buttonPanel.add(refreshButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(clearButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createFormSection(String title, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(142, 68, 173));
        panel.add(label, BorderLayout.NORTH);

        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    private JComponent createStudentSelector() {
        studentComboBox = new JComboBox<>();
        studentComboBox.setPreferredSize(new Dimension(300, 35));
        studentComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        studentComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    Student student = (Student) value;
                    setText(student.getStudentId() + " - " + student.getStudentName());
                }
                return this;
            }
        });
        studentComboBox.addActionListener(e -> onStudentSelected());

        return studentComboBox;
    }

    private JComponent createPlanSelector() {
        planComboBox = new JComboBox<>();
        planComboBox.setPreferredSize(new Dimension(300, 35));
        planComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        planComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LearningPlan) {
                    LearningPlan plan = (LearningPlan) value;
                    setText(plan.getPlanId() + " - " + plan.getIeltsType() + " (Target: " + plan.getTargetBand() + ")");
                }
                return this;
            }
        });

        return planComboBox;
    }

    private JComponent createAvailabilityDisplay() {
        availabilityTextArea = new JTextArea(4, 20);
        availabilityTextArea.setEditable(false);
        availabilityTextArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        availabilityTextArea.setBackground(new Color(245, 245, 245));
        availabilityTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        availabilityTextArea.setText("No availability data");

        JScrollPane scrollPane = new JScrollPane(availabilityTextArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));

        return scrollPane;
    }

    private JComponent createPreferenceDisplay() {
        preferenceTextArea = new JTextArea(4, 20);
        preferenceTextArea.setEditable(false);
        preferenceTextArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        preferenceTextArea.setBackground(new Color(245, 245, 245));
        preferenceTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        preferenceTextArea.setText("No preference data");

        JScrollPane scrollPane = new JScrollPane(preferenceTextArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));

        return scrollPane;
    }

    private JPanel createOutputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 20, 20, 20),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(142, 68, 173), 2),
                        "Output - Generated Schedule",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(142, 68, 173)
                )
        ));

        // Status and summary panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("No schedule generated yet. Please select student, plan and click 'Generate Schedule'.");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        statusLabel.setForeground(Color.GRAY);

        summaryLabel = new JLabel(" ");
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        summaryLabel.setForeground(new Color(52, 73, 94));

        infoPanel.add(statusLabel);
        infoPanel.add(summaryLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Session #", "Date & Time", "Day", "Duration", "Room", "Center", "Package", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(30);
        scheduleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        scheduleTable.getTableHeader().setBackground(new Color(142, 68, 173));
        scheduleTable.getTableHeader().setForeground(Color.BLACK);
        scheduleTable.setFont(new Font("Arial", Font.PLAIN, 11));
        scheduleTable.setSelectionBackground(new Color(232, 218, 239));

        // Set column widths
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Session #
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Date & Time
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Day
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Duration
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Room
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Center
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Package
        scheduleTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Status

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    // Event Handlers

    private void loadStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            studentComboBox.removeAllItems();

            for (Student student : students) {
                studentComboBox.addItem(student);
            }

            if (students.isEmpty()) {
                statusLabel.setText("⚠ No students found in the database.");
                statusLabel.setForeground(new Color(243, 156, 18));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onStudentSelected() {
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        if (selectedStudent == null) {
            availabilityTextArea.setText("No availability data");
            preferenceTextArea.setText("No preference data");
            planComboBox.removeAllItems();
            return;
        }

        // Load student availability
        loadStudentAvailability(selectedStudent.getStudentId());

        // Load student preferences
        loadStudentPreferences(selectedStudent.getStudentId());

        // Load learning plans for this student
        loadLearningPlans(selectedStudent.getStudentId());
    }

    private void loadStudentAvailability(String studentId) {
        try {
            List<StudentAvailability> availabilities = studentAvailabilityService.getAvailabilityByStudentId(studentId);

            if (availabilities.isEmpty()) {
                availabilityTextArea.setText("No availability set\n(All times available)");
            } else {
                StringBuilder sb = new StringBuilder();
                for (StudentAvailability avail : availabilities) {
                    sb.append(String.format("%s: %s - %s\n",
                            avail.getDayOfWeek(),
                            avail.getStartTime(),
                            avail.getEndTime()));
                }
                availabilityTextArea.setText(sb.toString());
            }
        } catch (Exception e) {
            availabilityTextArea.setText("Error loading availability");
            e.printStackTrace();
        }
    }

    private void loadStudentPreferences(String studentId) {
        try {
            List<StudentPreference> preferences = studentPreferenceService.getPreferenceByStudent(studentId);

            if (preferences.isEmpty()) {
                preferenceTextArea.setText("No preferences set");
            } else {
                StringBuilder sb = new StringBuilder();
                for (StudentPreference pref : preferences) {
                    sb.append(String.format("Center: %s\n", pref.getPreferredCenter()));
                    sb.append(String.format("%s: %s - %s\n",
                            pref.getDayOfWeek(),
                            pref.getPreferredStart(),
                            pref.getPreferredEnd()));
                }
                preferenceTextArea.setText(sb.toString());
            }
        } catch (Exception e) {
            preferenceTextArea.setText("Error loading preferences");
            e.printStackTrace();
        }
    }

    private void loadLearningPlans(String studentId) {
        try {
            planComboBox.removeAllItems();
            LearningPlan plan = learningPlanService.getLearningPlanByStudentId(studentId);
            if (plan != null) {
                planComboBox.addItem(plan);
                summaryLabel.setText(" ");
            } else {
                summaryLabel.setText("⚠ No learning plans found for this student");
                summaryLabel.setForeground(new Color(243, 156, 18));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshStudentData() {
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        if (selectedStudent != null) {
            onStudentSelected();
            statusLabel.setText("✓ Data refreshed successfully");
            statusLabel.setForeground(new Color(46, 204, 113));
        }
    }

    private void generateSchedule() {
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        LearningPlan selectedPlan = (LearningPlan) planComboBox.getSelectedItem();

        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student first",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedPlan == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a learning plan",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show loading state
        statusLabel.setText("⏳ Generating schedule using Hill-Climbing algorithm...");
        statusLabel.setForeground(new Color(243, 156, 18));
        summaryLabel.setText("Processing... Please wait.");
        tableModel.setRowCount(0);

        // Run scheduler in background
        SwingWorker<List<Scheduler.ScheduleProposal>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Scheduler.ScheduleProposal> doInBackground() throws Exception {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("STARTING HILL-CLIMBING SCHEDULER");
                System.out.println("Student: " + selectedStudent.getStudentName() + " (ID: " + selectedStudent.getStudentId() + ")");
                System.out.println("Plan: " + selectedPlan.getPlanId() + " - " + selectedPlan.getIeltsType());
                System.out.println("Target Band: " + selectedPlan.getTargetBand());
                System.out.println("Total Sessions: " + selectedPlan.getTotalSessions());
                System.out.println("=".repeat(80));

                // Run Hill-Climbing algorithm
                List<Scheduler.ScheduleProposal> proposals = scheduler.createOptimalSchedule(selectedPlan.getPlanId());

                System.out.println("\n" + "=".repeat(80));
                System.out.println("HILL-CLIMBING COMPLETED");
                System.out.println("Generated " + proposals.size() + " schedule proposals");
                System.out.println("=".repeat(80) + "\n");

                return proposals;
            }

            @Override
            protected void done() {
                try {
                    List<Scheduler.ScheduleProposal> proposals = get();

                    if (proposals != null && !proposals.isEmpty()) {
                        displaySchedule(proposals, selectedPlan);
                        statusLabel.setText("✓ Schedule generated successfully!");
                        statusLabel.setForeground(new Color(46, 204, 113));
                        summaryLabel.setText(String.format("📊 Generated %d sessions | Target: %d sessions | Plan: %s",
                                proposals.size(), selectedPlan.getTotalSessions(), selectedPlan.getPlanId()));
                        summaryLabel.setForeground(new Color(52, 73, 94));
                    } else {
                        statusLabel.setText("⚠ No schedule could be generated. Check console for details.");
                        statusLabel.setForeground(new Color(231, 76, 60));
                        summaryLabel.setText("❌ Generation failed - see console for constraints violations");
                        JOptionPane.showMessageDialog(SchedulerPanel.this,
                                "Could not generate schedule. Please check:\n" +
                                        "- Student availability\n" +
                                        "- Mentor availability\n" +
                                        "- Room availability\n" +
                                        "- Console logs for detailed errors",
                                "Scheduling Failed",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("✗ Error: " + e.getMessage());
                    statusLabel.setForeground(new Color(231, 76, 60));
                    summaryLabel.setText("❌ An error occurred during generation");
                    JOptionPane.showMessageDialog(SchedulerPanel.this,
                            "Error generating schedule: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void displaySchedule(List<Scheduler.ScheduleProposal> proposals, LearningPlan plan) {
        tableModel.setRowCount(0);

        // Get package info for display
        String packageInfo = getPackageInfo(plan);

        int sessionNumber = 1;
        for (Scheduler.ScheduleProposal proposal : proposals) {
            // Get room info: use proposal.roomId (mới)
            Room room = null;
            if (proposal.roomId != null) {
                room = roomService.getRoomById(proposal.roomId);
            }
            String roomName = room != null ? room.getRoomName() : "TBD";

            // Get center info
            Center center = centerService.getCenterById(proposal.centerId);
            String centerName = center != null ? center.getCenterName() : (proposal.centerId != null ? proposal.centerId : "TBD");

            // Format date and time
            String dateTime = proposal.scheduledTime.format(dateFormatter);
            String dayOfWeek = proposal.scheduledTime.getDayOfWeek().toString();

            Object[] row = {
                    "Session " + sessionNumber,
                    dateTime,
                    dayOfWeek,
                    "3 hours",
                    roomName,
                    centerName,
                    packageInfo,
                    "Scheduled"
            };
            tableModel.addRow(row);
            sessionNumber++;
        }
    }

    private String getPackageInfo(LearningPlan plan) {
        try {
            List<Package> packages = packageService.getAllPackages();
            for (Package pkg : packages) {
                if (pkg.getIeltsType().name().equals(plan.getIeltsType().name()) &&
                        Math.abs(pkg.getTargetBand() - plan.getTargetBand()) < 0.5 &&
                        pkg.getTotalSessions() == plan.getTotalSessions()) {
                    return pkg.getPackageName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Standard Package";
    }

    private String extractRoomIdFromLocation(String location) {
        // This is a placeholder - adjust based on how location is stored
        // For now, assuming location contains room ID or we need to find available room
        if (location != null && !location.isEmpty()) {
            return location;
        }
        return null;
    }

    private void clearResults() {
        tableModel.setRowCount(0);
        statusLabel.setText("No schedule generated yet. Please select student, plan and click 'Generate Schedule'.");
        statusLabel.setForeground(Color.GRAY);
        summaryLabel.setText(" ");
    }
}
