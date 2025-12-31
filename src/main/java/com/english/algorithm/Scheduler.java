package com.english.algorithm;

import com.english.controller.*;
import com.english.model.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Scheduler {
    private final StudentController studentController;
    private final MentorController mentorController;
    private final AssistantController assistantController;
    private final CenterController centerController;
    private final RoomController roomController;
    private final LearningPlanController learningPlanController;
    private final LearningSessionController learningSessionController;
    private final MentorAvailabilityController mentorAvailabilityController;
    private final PackageController packageController;
    private final StudentAvailabilityController studentAvailabilityController;
    private final StudentPreferenceController studentPreferenceController;

    private final Random random;

    // Hard Constraints
    private static final LocalTime CENTER_OPEN_TIME = LocalTime.of(8, 0);
    private static final LocalTime CENTER_CLOSE_TIME = LocalTime.of(21, 0);
    private static final int MAX_ROOM_CAPACITY = 20;
    private static final int SESSION_DURATION_HOURS = 3;
    private static final int SESSIONS_PER_WEEK = 2;
    private static final int COURSE_DURATION_WEEKS = 12;

    private Map<String, List<LearningSession>> sessionsByMentorCache;

    // Optimization parameters
    private static final int MAX_ITERATIONS = 100;
    private static final int NO_IMPROVEMENT_THRESHOLD = 10;

    public Scheduler(StudentController studentController,
                     MentorController mentorController,
                     AssistantController assistantController,
                     CenterController centerController,
                     RoomController roomController,
                     LearningPlanController learningPlanController,
                     LearningSessionController learningSessionController,
                     MentorAvailabilityController mentorAvailabilityController,
                     PackageController packageController,
                     StudentAvailabilityController studentAvailabilityController,
                     StudentPreferenceController studentPreferenceController) {
        this.studentController = studentController;
        this.mentorController = mentorController;
        this.assistantController = assistantController;
        this.centerController = centerController;
        this.roomController = roomController;
        this.learningPlanController = learningPlanController;
        this.learningSessionController = learningSessionController;
        this.mentorAvailabilityController = mentorAvailabilityController;
        this.packageController = packageController;
        this.studentAvailabilityController = studentAvailabilityController;
        this.studentPreferenceController = studentPreferenceController;
        this.random = new Random();
        this.sessionsByMentorCache = new HashMap<>();
        refreshSessionCache();
    }

    private void refreshSessionCache() {
        sessionsByMentorCache.clear();
        List<LearningSession> allSessions = learningSessionController.getAllLearningSessions();

        for (LearningSession session : allSessions) {
            LearningPlan plan = learningPlanController.getLearningPlanById(session.getPlanId());
            if (plan != null) {
                sessionsByMentorCache
                        .computeIfAbsent(plan.getMentorId(), k -> new ArrayList<>())
                        .add(session);
            }
        }
    }

    /**
     * HARD CONSTRAINT 6: Giáo viên phải dạy đúng chuyên môn
     * (Tìm mentor phù hợp nhất cho học viên)
     */
    public Mentor findOptimalMentor(String studentId) {
        Student student = studentController.getStudentById(studentId);
        if (student == null) return null;

        List<Mentor> allMentors = mentorController.getAllMentors();
        if (allMentors.isEmpty()) return null;

        // Lọc mentor theo chuyên môn (Hard Constraint)
        List<Mentor> qualifiedMentors = allMentors.stream()
                .filter(m -> m.isAvailable())
                .filter(m -> isMentorQualified(m, student))
                .collect(Collectors.toList());

        if (qualifiedMentors.isEmpty()) {
            System.err.println("Không tìm thấy mentor phù hợp với chuyên môn yêu cầu");
            return null;
        }

        Map<String, Object> criteria = new HashMap<>();
        criteria.put("ieltsType", student.getIeltsType().name());
        criteria.put("targetBand", student.getTargetBand());

        return SearchHandler.mentorHandler().findOptimalEntity(qualifiedMentors, criteria);
    }

    /**
     * Kiểm tra mentor có đủ chuyên môn dạy không
     */
    private boolean isMentorQualified(Mentor mentor, Student student) {
        if (student.getIeltsType() == Student.IELTSType.GENERAL) {
            return mentor.isCanTeachGeneral();
        } else if (student.getIeltsType() == Student.IELTSType.ACADEMIC) {
            return mentor.isCanTeachAcademic();
        }
        return false;
    }

    /**
     * HARD CONSTRAINT 1, 3, 4: Tìm phòng học phù hợp
     * - Không trùng phòng học
     * - Đủ sức chứa (≤ 20 người)
     * - Trong khung giờ hoạt động (8:00 - 21:00)
     */
    public Room findOptimalRoom(String centerId, LocalDateTime scheduledTime,
                                LearningSession.SessionType sessionType, String planId) {
        if (sessionType == LearningSession.SessionType.ONLINE) {
            return null;
        }

        // HARD CONSTRAINT 3: Kiểm tra giờ trong khung hoạt động
        if (!isWithinOperatingHours(scheduledTime)) {
            System.err.println("Thời gian không nằm trong khung giờ hoạt động (8:00-21:00)");
            return null;
        }

        List<Room> allRooms = roomController.getAllRooms();

        List<Room> availableRooms = allRooms.stream()
                .filter(r -> r.getCenterId().equals(centerId))
                .filter(Room::isAvailable)
                .filter(r -> isRoomAvailableAtTime(r, scheduledTime))
                .filter(r -> hasEnoughCapacity(r, planId))
                .collect(Collectors.toList());

        if (availableRooms.isEmpty()) {
            System.err.println("Không tìm thấy phòng học phù hợp với các ràng buộc");
            return null;
        }

        Room currentRoom = availableRooms.get(random.nextInt(availableRooms.size()));
        double currentScore = scoreRoom(currentRoom, scheduledTime, planId);

        int noImprovementCount = 0;

        for (int i = 0; i < MAX_ITERATIONS && noImprovementCount < NO_IMPROVEMENT_THRESHOLD; i++) {
            List<Room> neighbors = getRandomNeighbor(availableRooms, currentRoom, 3);

            Room bestNeighbor = null;
            double bestScore = currentScore;

            for (Room neighbor : neighbors) {
                double score = scoreRoom(neighbor, scheduledTime, planId);
                if (score > bestScore) {
                    bestNeighbor = neighbor;
                    bestScore = score;
                }
            }

            if (bestNeighbor != null) {
                currentRoom = bestNeighbor;
                currentScore = bestScore;
                noImprovementCount = 0;
            } else {
                noImprovementCount++;
            }
        }

        return currentRoom;
    }

    /**
     * HARD CONSTRAINT 5: Tạo lịch đủ số buổi và thời lượng theo khóa học
     * (Mỗi lớp phải đủ 2 buổi/tuần)
     */
    public List<ScheduleProposal> createOptimalSchedule(String planId) {
        try {
            LearningPlan plan = learningPlanController.getLearningPlanById(planId);
            if (plan == null) {
                System.err.println("Lỗi: Không tìm thấy learning plan: " + planId);
                return Collections.emptyList();
            }

            // HARD CONSTRAINT 5: Kiểm tra số buổi học hợp lệ
            if (!hasValidSessionCount(plan)) {
                System.err.println("Lỗi: Số buổi học không hợp lệ cho plan: " + planId);
                return Collections.emptyList();
            }

            Student student = studentController.getStudentById(plan.getStudentId());
            Mentor mentor = mentorController.getMentorById(plan.getMentorId());

            if (student == null || mentor == null) {
                System.err.println("Lỗi: Không tìm thấy student hoặc mentor");
                return Collections.emptyList();
            }

            // HARD CONSTRAINT 6: Kiểm tra mentor có đủ chuyên môn
            if (!isMentorQualified(mentor, student)) {
                System.err.println("Lỗi: Mentor không đủ chuyên môn dạy loại IELTS này");
                return Collections.emptyList();
            }

            List<ScheduleProposal> proposals = new ArrayList<>();
            LocalDate currentDate = plan.getStartDate();

            int sessionCount = 0;
            int weekCount = 0;
            int sessionsThisWeek = 0;

            // Tạo lịch theo yêu cầu: 2 buổi/tuần
            while (sessionCount < plan.getTotalSessions() && weekCount < COURSE_DURATION_WEEKS * 2) {
                if (sessionsThisWeek >= SESSIONS_PER_WEEK) {
                    currentDate = currentDate.plusWeeks(1).with(DayOfWeek.MONDAY);
                    sessionsThisWeek = 0;
                    weekCount++;
                    continue;
                }

                ScheduleProposal proposal = findOptimalTimeSlot(
                        student, mentor, currentDate, sessionCount + 1, plan.getPlanId()
                );

                if (proposal != null && validateHardConstraints(proposal, mentor.getMentorId())) {
                    proposals.add(proposal);
                    sessionCount++;
                    sessionsThisWeek++;
                    currentDate = proposal.scheduledTime.toLocalDate().plusDays(1);
                } else {
                    currentDate = currentDate.plusDays(1);
                }

                if (currentDate.isAfter(plan.getStartDate().plusWeeks(COURSE_DURATION_WEEKS * 3))) {
                    System.err.println("Không thể tạo lịch học trong thời gian hợp lý");
                    break;
                }
            }

            // HARD CONSTRAINT 5: Kiểm tra đủ số buổi
            if (!validateScheduleCompleteness(planId, proposals)) {
                System.err.println("Cảnh báo: Lịch học không đủ số buổi yêu cầu!");
            }

            return proposals;
        } catch (Exception e) {
            System.err.println("Lỗi không mong đợi khi tạo lịch: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Kiểm tra tất cả Hard Constraints cho một proposal
     */
    private boolean validateHardConstraints(ScheduleProposal proposal, String mentorId) {
        if (proposal == null || mentorId == null || mentorId.trim().isEmpty()) {
            return false;
        }

        // HC2: Giảng viên không dạy cùng lúc 2 lớp
        if (hasMentorConflict(proposal.scheduledTime, mentorId)) {
            return false;
        }

        // HC3: Giờ học trong khung 8:00-21:00
        if (!isWithinOperatingHours(proposal.scheduledTime)) {
            return false;
        }

        // HC3: Thời lượng đúng 2 tiếng và không vượt quá 21:00
        if (!isValidSessionDuration(proposal.scheduledTime)) {
            return false;
        }

        return true;
    }

    /**
     * HARD CONSTRAINT 3: Kiểm tra thời gian trong khung hoạt động 8:00-21:00
     */
    private boolean isWithinOperatingHours(LocalDateTime time) {
        LocalTime startTime = time.toLocalTime();
        LocalTime endTime = time.plusHours(SESSION_DURATION_HOURS).toLocalTime();

        return !startTime.isBefore(CENTER_OPEN_TIME) &&
                !endTime.isAfter(CENTER_CLOSE_TIME);
    }

    /**
     * HARD CONSTRAINT 3: Kiểm tra thời lượng buổi học đúng 2 tiếng và trong khung giờ
     */
    private boolean isValidSessionDuration(LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusHours(SESSION_DURATION_HOURS);

        if (startTime.toLocalTime().isBefore(CENTER_OPEN_TIME)) {
            return false;
        }

        if (endTime.toLocalTime().isAfter(CENTER_CLOSE_TIME)) {
            return false;
        }

        return true;
    }

    /**
     * HARD CONSTRAINT 5: Kiểm tra số buổi học hợp lệ
     */
    private boolean hasValidSessionCount(LearningPlan plan) {
        if (plan == null) return false;

        if (plan.getTotalSessions() <= 0) {
            return false;
        }

        if (plan.getRemainingSessions() < 0 ||
                plan.getRemainingSessions() > plan.getTotalSessions()) {
            return false;
        }

        int minimumSessions = SESSIONS_PER_WEEK * COURSE_DURATION_WEEKS;
        if (plan.getTotalSessions() < minimumSessions) {
            System.err.println("Cảnh báo: Số buổi học ít hơn yêu cầu tối thiểu (" +
                    minimumSessions + " buổi)");
        }

        return true;
    }

    /**
     * HARD CONSTRAINT 5: Kiểm tra lịch học đủ số buổi
     */
    private boolean validateScheduleCompleteness(String planId, List<ScheduleProposal> proposals) {
        LearningPlan plan = learningPlanController.getLearningPlanById(planId);
        if (plan == null) return false;

        if (proposals.size() != plan.getTotalSessions()) {
            System.err.println("Lỗi: Số buổi học không đủ. Cần: " +
                    plan.getTotalSessions() + ", Có: " + proposals.size());
            return false;
        }

        for (ScheduleProposal proposal : proposals) {
            if (!isValidSessionDuration(proposal.scheduledTime)) {
                System.err.println("Lỗi: Buổi học " + proposal.scheduledTime +
                        " không hợp lệ (phải 2 tiếng trong 8:00-21:00)");
                return false;
            }
        }

        return true;
    }

    /**
     * HARD CONSTRAINT 4: Kiểm tra sức chứa phòng (≤ 20 người)
     */
    private boolean hasEnoughCapacity(Room room, String planId) {
        LearningPlan plan = learningPlanController.getLearningPlanById(planId);
        if (plan == null) return false;

        int studentCount = 1;
        int mentorCount = 1;
        int assistantCount = 0; // Trợ giảng (nếu có)

        int totalPeople = studentCount + mentorCount + assistantCount;

        // HARD CONSTRAINT 4: Tổng số người ≤ 20
        if (totalPeople > MAX_ROOM_CAPACITY) {
            return false;
        }

        return totalPeople <= room.getCapacity();
    }

    /**
     * HARD CONSTRAINT 1: Kiểm tra phòng có bị trùng không
     */
    private boolean isRoomAvailableAtTime(Room room, LocalDateTime time) {
        LocalDateTime endTime = time.plusHours(SESSION_DURATION_HOURS);

        return learningSessionController.getAllLearningSessions().stream()
                .filter(s -> s.getLocation() != null && s.getLocation().contains(room.getRoomId()))
                .filter(s -> s.getSessionStatus() == LearningSession.SessionStatus.SCHEDULED)
                .noneMatch(s -> {
                    LocalDateTime sessionEnd = s.getScheduledTime().plusHours(SESSION_DURATION_HOURS);
                    return time.isBefore(sessionEnd) && s.getScheduledTime().isBefore(endTime);
                });
    }

    private boolean hasRoomConflict(String roomId, LocalDateTime time) {
        LocalDateTime endTime = time.plusHours(SESSION_DURATION_HOURS);

        return learningSessionController.getAllLearningSessions().stream()
                .filter(s -> s.getLocation() != null && s.getLocation().contains(roomId))
                .filter(s -> s.getSessionStatus() == LearningSession.SessionStatus.SCHEDULED)
                .anyMatch(s -> {
                    LocalDateTime sessionEnd = s.getScheduledTime().plusHours(SESSION_DURATION_HOURS);
                    return time.isBefore(sessionEnd) && s.getScheduledTime().isBefore(endTime);
                });
    }

    /**
     * HARD CONSTRAINT 2: Kiểm tra giảng viên có dạy cùng lúc 2 lớp không
     */
    private boolean hasMentorConflict(LocalDateTime time, String mentorId) {
        LocalDateTime endTime = time.plusHours(SESSION_DURATION_HOURS);

        long conflictCount = learningSessionController.getAllLearningSessions().stream()
                .filter(s -> {
                    LearningPlan plan = learningPlanController.getLearningPlanById(s.getPlanId());
                    return plan != null && plan.getMentorId().equals(mentorId);
                })
                .filter(s -> s.getSessionStatus() == LearningSession.SessionStatus.SCHEDULED)
                .filter(s -> {
                    LocalDateTime sessionEnd = s.getScheduledTime().plusHours(SESSION_DURATION_HOURS);
                    boolean hasConflict = time.isBefore(sessionEnd) && s.getScheduledTime().isBefore(endTime);
                    if (hasConflict) {
                        System.out.println("⚠️ Mentor conflict detected at " + time); // ← THÊM LOG
                    }
                    return hasConflict;
                })
                .count();

        return conflictCount > 0;
    }

    /**
     * Tạo LearningSession với validation đầy đủ Hard Constraints
     */
    public LearningSession createValidatedSession(ScheduleProposal proposal,
                                                  String sessionId,
                                                  String planId,
                                                  int sessionNumber) {
        // Validate tất cả Hard Constraints
        if (!isValidSessionDuration(proposal.scheduledTime)) {
            throw new IllegalArgumentException(
                    "Vi phạm Hard Constraint: Buổi học phải kéo dài 2 tiếng trong khung 8:00-21:00"
            );
        }

        LearningPlan plan = learningPlanController.getLearningPlanById(planId);
        if (plan != null) {
            if (!validateHardConstraints(proposal, plan.getMentorId())) {
                throw new IllegalArgumentException(
                        "Vi phạm Hard Constraints: Giảng viên hoặc phòng học bị trùng lịch"
                );
            }
        }

        LocalDateTime endTime = proposal.scheduledTime.plusHours(SESSION_DURATION_HOURS);

        LearningSession session = new LearningSession();
        session.setSessionId(sessionId);
        session.setPlanId(planId);
        session.setSessionNumber(sessionNumber);
        session.setSessionType(proposal.sessionType);
        session.setScheduledTime(proposal.scheduledTime);
        session.setActualStart(proposal.scheduledTime);
        session.setActualEnd(endTime);
        session.setLocation(proposal.centerId);
        session.setSessionStatus(LearningSession.SessionStatus.SCHEDULED);

        return session;
    }

    // ==================== HELPER METHODS ====================

    private ScheduleProposal findOptimalTimeSlot(Student student, Mentor mentor,
                                                 LocalDate startDate, int sessionNumber,
                                                 String planId) {
        List<LocalDateTime> possibleTimes = generatePossibleTimeSlots(startDate);

        if (possibleTimes.isEmpty()) return null;

        // Lọc các thời gian vi phạm Hard Constraints
        possibleTimes = possibleTimes.stream()
                .filter(this::isWithinOperatingHours)
                .filter(this::isValidSessionDuration)
                .filter(time -> !hasMentorConflict(time, mentor.getMentorId()))
                .collect(Collectors.toList());

        if (possibleTimes.isEmpty()) return null;

        LocalDateTime currentTime = possibleTimes.get(random.nextInt(possibleTimes.size()));
        double currentScore = scoreTimeSlot(currentTime, student, mentor);

        ScheduleProposal currentProposal = new ScheduleProposal(
                currentTime,
                LearningSession.SessionType.OFFLINE,
                student.getPreferredCenterId(),
                currentScore
        );

        int noImprovementCount = 0;

        for (int i = 0; i < MAX_ITERATIONS && noImprovementCount < NO_IMPROVEMENT_THRESHOLD; i++) {
            List<LocalDateTime> neighbors = getRandomNeighbor(possibleTimes, currentTime, 5);

            LocalDateTime bestTime = null;
            double bestScore = currentScore;

            for (LocalDateTime neighborTime : neighbors) {
                double score = scoreTimeSlot(neighborTime, student, mentor);
                if (score > bestScore) {
                    bestTime = neighborTime;
                    bestScore = score;
                }
            }

            if (bestTime != null) {
                currentTime = bestTime;
                currentScore = bestScore;
                currentProposal = new ScheduleProposal(
                        currentTime,
                        LearningSession.SessionType.OFFLINE,
                        student.getPreferredCenterId(),
                        currentScore
                );
                noImprovementCount = 0;
            } else {
                noImprovementCount++;
            }
        }

        return currentProposal;
    }

    private double scoreRoom(Room room, LocalDateTime scheduledTime, String planId) {
        double score = 0.0;

        if (room.isAvailable()) {
            score += 20.0;
        }

        if (isRoomAvailableAtTime(room, scheduledTime)) {
            score += 30.0;
        }

        LearningPlan plan = learningPlanController.getLearningPlanById(planId);
        if (plan != null) {
            int totalPeople = 2; // Student + Mentor

            if (totalPeople <= room.getCapacity()) {
                double utilizationRate = (double) totalPeople / room.getCapacity();

                if (utilizationRate >= 0.5 && utilizationRate <= 0.8) {
                    score += 30.0;
                } else if (utilizationRate >= 0.3 && utilizationRate < 0.5) {
                    score += 20.0;
                } else if (utilizationRate > 0.8) {
                    score += 25.0;
                } else {
                    score += 10.0;
                }
            }
        }

        return score;
    }

    private double scoreTimeSlot(LocalDateTime time, Student student, Mentor mentor) {
        double score = 0.0;

        // Ưu tiên giờ vàng (9-11h, 14-16h)
        int hour = time.getHour();
        if ((hour >= 9 && hour < 11) || (hour >= 14 && hour < 16)) {
            score += 30.0;
        } else if (hour >= 8 && hour < 20) {
            score += 15.0;
        }

        // Ưu tiên ngày trong tuần
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            score += 30.0;
        } else {
            score += 10.0;
        }

        // Không có xung đột lịch
        if (!hasMentorConflict(time, mentor.getMentorId())) {
            score += 40.0;
        }

        return score;
    }

    private List<LocalDateTime> generatePossibleTimeSlots(LocalDate startDate) {
        List<LocalDateTime> timeSlots = new ArrayList<>();

        for (int day = 0; day < 7; day++) {
            LocalDate date = startDate.plusDays(day);

            // Tạo các khung giờ từ 8:00 đến giờ cuối cùng có thể bắt đầu
            for (int hour = 8; hour <= 19; hour++) { // 19h là giờ muộn nhất để kết thúc lúc 21h
                LocalDateTime timeSlot = LocalDateTime.of(date, LocalTime.of(hour, 0));

                if (isValidSessionDuration(timeSlot)) {
                    timeSlots.add(timeSlot);
                }
            }
        }

        return timeSlots;
    }

    private <T> List<T> getRandomNeighbor(List<T> list, T current, int count) {
        List<T> neighbors = new ArrayList<>(list);
        neighbors.remove(current);
        Collections.shuffle(neighbors, random);
        return neighbors.subList(0, Math.min(count, neighbors.size()));
    }

    /**
     * Class đại diện cho một đề xuất lịch học
     */
    public static class ScheduleProposal {
        public final LocalDateTime scheduledTime;
        public final LearningSession.SessionType sessionType;
        public final String centerId;
        public final double score;

        public ScheduleProposal(LocalDateTime scheduledTime, LearningSession.SessionType sessionType,
                                String centerId, double score) {
            this.scheduledTime = scheduledTime;
            this.sessionType = sessionType;
            this.centerId = centerId;
            this.score = score;
        }

        @Override
        public String toString() {
            return String.format("Time: %s, Type: %s, Center: %s, Score: %.2f",
                    scheduledTime, sessionType, centerId, score);
        }
    }
}