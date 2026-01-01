package com.english.algorithm;

import com.english.controller.*;
import com.english.model.*;
import com.english.model.Package;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class Scheduler - Tạo lịch học tối ưu cho học viên
 * 
 * Sử dụng các controller sau:
 * - studentController: Lấy thông tin học viên
 * - mentorController: Lấy thông tin giảng viên
 * - assistantController: Tìm assistant phù hợp và tính capacity
 * - centerController: Validate center và lấy thông tin center
 * - roomController: Tìm phòng học phù hợp
 * - learningPlanController: Lấy thông tin learning plan
 * - learningSessionController: Quản lý learning sessions
 * - packageController: Validate plan với package hợp lệ
 * - studentPreferenceController: Lấy preferences của học viên để tối ưu scheduling
 * - mentorAvailabilityController: Lấy availability của mentor để filter và score time slots
 * - studentAvailabilityController: Lấy availability của student để filter và score time slots
 */
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

        // Sử dụng centerController để validate center tồn tại
        if (centerId != null && !centerController.centerExists(centerId)) {
            System.err.println("Center không tồn tại: " + centerId);
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

            // Sử dụng packageController để validate plan có khớp với package hợp lệ không
            if (!validatePlanAgainstPackages(plan)) {
                System.err.println("Cảnh báo: Plan không khớp với package hợp lệ nào");
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
            int sessionsThisWeek = 0;

            int maxWeeks = (plan.getTotalSessions() / SESSIONS_PER_WEEK) + 2;
            int weeksSearched = 0;

            // Tạo lịch theo yêu cầu: 2 buổi/tuần
            while (sessionCount < plan.getTotalSessions() && weeksSearched < maxWeeks * 2) {
                if (sessionsThisWeek >= SESSIONS_PER_WEEK) {
                    currentDate = currentDate.plusWeeks(1).with(DayOfWeek.MONDAY);
                    sessionsThisWeek = 0;
                    weeksSearched++;
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

                if (currentDate.isAfter(plan.getStartDate().plusWeeks(maxWeeks * 2))) {
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
     * Sử dụng packageController để validate plan có khớp với package hợp lệ
     */
    private boolean validatePlanAgainstPackages(LearningPlan plan) {
        if (plan == null) return false;
        
        List<Package> allPackages = packageController.getAllPackages();
        
        // Tìm package phù hợp với plan
        return allPackages.stream()
                .filter(Package::isActive)
                .anyMatch(pkg -> 
                    pkg.getIeltsType().name().equals(plan.getIeltsType().name()) &&
                    Math.abs(pkg.getTargetBand() - plan.getTargetBand()) < 0.5 && // Cho phép sai số nhỏ
                    pkg.getTotalSessions() == plan.getTotalSessions()
                );
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
     * Sử dụng assistantController để tìm assistant phù hợp và tính vào capacity
     */
    private boolean hasEnoughCapacity(Room room, String planId) {
        LearningPlan plan = learningPlanController.getLearningPlanById(planId);
        if (plan == null) return false;

        int studentCount = 1;
        int mentorCount = 1;
        
        // Sử dụng assistantController để tìm assistant phù hợp
        int assistantCount = findSuitableAssistantCount(plan);
        
        int totalPeople = studentCount + mentorCount + assistantCount;

        // HARD CONSTRAINT 4: Tổng số người ≤ 20
        if (totalPeople > MAX_ROOM_CAPACITY) {
            return false;
        }

        return totalPeople <= room.getCapacity();
    }

    /**
     * Tìm số lượng assistant phù hợp cho plan
     * Sử dụng assistantController để tìm assistant có thể hỗ trợ loại IELTS phù hợp
     */
    private int findSuitableAssistantCount(LearningPlan plan) {
        if (plan == null) return 0;
        
        Student student = studentController.getStudentById(plan.getStudentId());
        if (student == null) return 0;
        
        List<Assistant> allAssistants = assistantController.getAllAssistants();
        
        // Tìm assistant phù hợp với loại IELTS của học viên
        long suitableAssistants = allAssistants.stream()
                .filter(Assistant::isAvailable)
                .filter(a -> {
                    if (student.getIeltsType() == Student.IELTSType.GENERAL) {
                        return a.isCanSupportGeneral();
                    } else if (student.getIeltsType() == Student.IELTSType.ACADEMIC) {
                        return a.isCanSupportAcademic();
                    }
                    return false;
                })
                .count();
        
        // Có thể có tối đa 1 assistant cho mỗi session
        return suitableAssistants > 0 ? 1 : 0;
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

        // Sử dụng studentPreferenceController để lọc theo preferences nếu có
        List<StudentPreference> preferences = studentPreferenceController.getPreferenceByStudent(student.getStudentId());
        String preferredCenterId = student.getPreferredCenterId();
        
        // Nếu có preferences, ưu tiên center từ preferences
        if (!preferences.isEmpty()) {
            String prefCenter = preferences.get(0).getPreferredCenter();
            if (prefCenter != null && !prefCenter.trim().isEmpty()) {
                // Sử dụng centerController để validate center
                if (centerController.centerExists(prefCenter)) {
                    preferredCenterId = prefCenter;
                }
            }
        }
        
        // Validate center tồn tại
        if (preferredCenterId != null && !centerController.centerExists(preferredCenterId)) {
            System.err.println("Preferred center không tồn tại: " + preferredCenterId);
            // Tìm center hợp lệ khác
            List<Center> allCenters = centerController.getAllCenters();
            if (!allCenters.isEmpty()) {
                preferredCenterId = allCenters.get(0).getCenterId();
            } else {
                preferredCenterId = null;
            }
        }

        // Lọc các thời gian vi phạm Hard Constraints
        possibleTimes = possibleTimes.stream()
                .filter(this::isWithinOperatingHours)
                .filter(this::isValidSessionDuration)
                .filter(time -> !hasMentorConflict(time, mentor.getMentorId()))
                .filter(time -> isMentorAvailableAtTime(mentor.getMentorId(), time))
                .filter(time -> isStudentAvailableAtTime(student.getStudentId(), time))
                .collect(Collectors.toList());

        if (possibleTimes.isEmpty()) return null;

        LocalDateTime currentTime = possibleTimes.get(random.nextInt(possibleTimes.size()));
        double currentScore = scoreTimeSlot(currentTime, student, mentor);

        // SC2: Tìm center gần nhất với địa chỉ học viên - Soft Constraint 2
        String bestCenterId = findNearestCenterToStudent(student, preferredCenterId);
        
        ScheduleProposal currentProposal = new ScheduleProposal(
                currentTime,
                LearningSession.SessionType.OFFLINE,
                bestCenterId != null ? bestCenterId : preferredCenterId,
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
                        bestCenterId != null ? bestCenterId : preferredCenterId,
                        currentScore
                );
                noImprovementCount = 0;
            } else {
                noImprovementCount++;
            }
        }

        return currentProposal;
    }

    /**
     * SC2: Tìm center gần nhất với địa chỉ học viên - Soft Constraint 2
     */
    private String findNearestCenterToStudent(Student student, String preferredCenterId) {
        if (student == null || student.getStudentAddress() == null) {
            return preferredCenterId;
        }
        
        List<Center> allCenters = centerController.getAllCenters();
        if (allCenters.isEmpty()) {
            return preferredCenterId;
        }
        
        // Ưu tiên center gần địa chỉ học viên
        Center nearestCenter = null;
        int maxScore = 0;
        
        for (Center center : allCenters) {
            int score = 0;
            
            // Kiểm tra center có gần địa chỉ học viên không
            if (isCenterNearStudentAddress(student.getStudentAddress(), center)) {
                score += 100; // Điểm cao nhất cho center gần
            }
            
            // Nếu là center ưa thích, thêm điểm
            if (preferredCenterId != null && preferredCenterId.equals(center.getCenterId())) {
                score += 50;
            }
            
            if (score > maxScore) {
                maxScore = score;
                nearestCenter = center;
            }
        }
        
        // Nếu tìm được center gần, trả về center đó
        if (nearestCenter != null && maxScore > 0) {
            return nearestCenter.getCenterId();
        }
        
        // Nếu không, trả về preferred center hoặc center đầu tiên
        if (preferredCenterId != null && centerController.centerExists(preferredCenterId)) {
            return preferredCenterId;
        }
        
        return allCenters.get(0).getCenterId();
    }

    private int roomUsageCount(String roomId, LocalDate from, LocalDate to) {
        return (int) learningSessionController.getAllLearningSessions().stream()
                .filter(s -> s.getLocation() != null && s.getLocation().contains(roomId))
                .filter(s -> {
                    LocalDate d = s.getScheduledTime().toLocalDate();
                    return (!d.isBefore(from) && !d.isAfter(to));
                }).count();
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
            // Sử dụng assistantController để tính chính xác số người
            int assistantCount = findSuitableAssistantCount(plan);
            int totalPeople = 2 + assistantCount; // Student + Mentor + Assistant(s)

            if (totalPeople <= room.getCapacity()) {
                double utilizationRate = (double) totalPeople / room.getCapacity();

                // SC4: Ưu tiên phòng học đều được sử dụng - Soft Constraint 4
                if (utilizationRate >= 0.5 && utilizationRate <= 0.8) {
                    score += 40.0; // Tăng điểm cho utilization tốt (50-80%)
                } else if (utilizationRate >= 0.3 && utilizationRate < 0.5) {
                    score += 25.0; // Điểm vừa cho utilization thấp (30-50%)
                } else if (utilizationRate > 0.8 && utilizationRate <= 0.95) {
                    score += 30.0; // Điểm tốt cho utilization cao nhưng không quá đông (80-95%)
                } else if (utilizationRate > 0.95) {
                    score -= 20.0; // Penalty cho phòng quá đông (>95%)
                } else {
                    score -= 15.0; // Penalty cho phòng trống (<30%)
                }
            }
            
            // SC2: Ưu tiên center gần nơi cư trú của học viên - Soft Constraint 2
            Student student = studentController.getStudentById(plan.getStudentId());
            if (student != null) {
                Center center = centerController.getCenterById(room.getCenterId());
                if (center != null) {
                    score += 5.0; // Base điểm cho center hợp lệ
                    
                    // Kiểm tra center có gần địa chỉ học viên không
                    if (isCenterNearStudentAddress(student.getStudentAddress(), center)) {
                        score += 35.0; // Bonus lớn nếu center gần
                    } else if (student.getPreferredCenterId() != null && 
                               student.getPreferredCenterId().equals(center.getCenterId())) {
                        score += 20.0; // Bonus nếu là center ưa thích
                    }
                }
            }
        }

        int usage = roomUsageCount(room.getRoomId(), LocalDate.now().minusWeeks(1), LocalDate.now());
        if (usage > 5) score -= (usage - 5) * 5;

        return score;
    }

    /**
     * SC2: Kiểm tra center có gần địa chỉ học viên không - Soft Constraint 2
     * So sánh đơn giản dựa trên tên quận/huyện trong địa chỉ
     */
    private boolean isCenterNearStudentAddress(String studentAddress, Center center) {
        if (studentAddress == null || center == null || center.getAddress() == null) {
            return false;
        }
        
        // Lấy tên quận/huyện từ địa chỉ (ví dụ: "Quận 9", "Bình Thạnh")
        String studentDistrict = extractDistrict(studentAddress);
        String centerDistrict = extractDistrict(center.getAddress());
        
        // Nếu có city, cũng kiểm tra city
        if (center.getCity() != null) {
            String centerCityDistrict = extractDistrict(center.getCity());
            if (studentDistrict != null && !studentDistrict.isEmpty()) {
                if (studentDistrict.equalsIgnoreCase(centerDistrict) || 
                    studentDistrict.equalsIgnoreCase(centerCityDistrict)) {
                    return true;
                }
            }
        }
        
        // So sánh trực tiếp district
        if (studentDistrict != null && centerDistrict != null && 
            !studentDistrict.isEmpty() && !centerDistrict.isEmpty()) {
            return studentDistrict.equalsIgnoreCase(centerDistrict);
        }
        
        return false;
    }

    /**
     * Trích xuất tên quận/huyện từ địa chỉ
     * Ví dụ: "123 Đường ABC, Quận 9, TP.HCM" -> "Quận 9"
     */
    private String extractDistrict(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "";
        }
        
        // Tìm pattern "Quận X", "Huyện X", "Q.X", etc.
        String[] parts = address.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.toLowerCase().contains("quận") || 
                trimmed.toLowerCase().contains("huyện") ||
                trimmed.toLowerCase().startsWith("q.") ||
                trimmed.toLowerCase().startsWith("q")) {
                return trimmed;
            }
        }
        
        // Nếu không tìm thấy, trả về phần cuối cùng (thường là quận/huyện)
        if (parts.length > 0) {
            return parts[parts.length - 1].trim();
        }
        
        return "";
    }

    private double scoreTimeSlot(LocalDateTime time, Student student, Mentor mentor) {
        double score = 0.0;

        // Sử dụng studentPreferenceController để lấy preferences của học viên
        List<StudentPreference> preferences = studentPreferenceController.getPreferenceByStudent(student.getStudentId());
        
        // Ưu tiên giờ vàng (9-11h, 14-16h)
        int hour = time.getHour();
        if ((hour >= 9 && hour < 11) || (hour >= 14 && hour < 16)) {
            score += 30.0;
        } else if (hour >= 8 && hour < 20) {
            score += 15.0;
        }

        // SC1: Ưu tiên ngày trong tuần (tránh cuối tuần)
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            score += 30.0;
        } else {
            // Penalty cho cuối tuần (Soft Constraint 1)
            score += 5.0; // Giảm điểm đáng kể cho cuối tuần
        }

        // SC1: Ưu tiên buổi tối (18:00-21:00) - Soft Constraint 1
        if (hour >= 18 && hour < 21) {
            score += 25.0; // Bonus điểm cho buổi tối
        }

        // Sử dụng student preferences để tăng điểm nếu khớp
        if (!preferences.isEmpty()) {
            for (StudentPreference pref : preferences) {
                // Kiểm tra ngày trong tuần có khớp không
                if (matchesDayOfWeek(dayOfWeek, pref.getDayOfWeek())) {
                    score += 20.0; // Bonus điểm nếu khớp ngày
                    
                    // Kiểm tra giờ có trong khoảng preferred không
                    LocalTime timeOfDay = time.toLocalTime();
                    if (timeOfDay.compareTo(pref.getPreferredStart()) >= 0 && 
                        timeOfDay.compareTo(pref.getPreferredEnd()) <= 0) {
                        score += 25.0; // Bonus điểm cao hơn nếu khớp cả giờ
                    }
                }
            }
        }

        // Sử dụng mentorAvailabilityController để tăng điểm nếu khớp với mentor availability
        List<MentorAvailability> mentorAvailabilities = mentorAvailabilityController.getAvailabilityByMentorId(mentor.getMentorId());
        if (!mentorAvailabilities.isEmpty()) {
            LocalTime timeOfDay = time.toLocalTime();
            LocalTime sessionEndTime = timeOfDay.plusHours(SESSION_DURATION_HOURS);
            for (MentorAvailability avail : mentorAvailabilities) {
                if (matchesDayOfWeek(dayOfWeek, avail.getDayOfWeek())) {
                    if (timeOfDay.compareTo(avail.getStartTime()) >= 0 && 
                        sessionEndTime.compareTo(avail.getEndTime()) <= 0) {
                        score += 30.0; // Bonus điểm cao nếu khớp với mentor availability
                    }
                }
            }
        }

        // Sử dụng studentAvailabilityController để tăng điểm nếu khớp với student availability
        List<StudentAvailability> studentAvailabilities = studentAvailabilityController.getAvailabilityByStudentId(student.getStudentId());
        if (!studentAvailabilities.isEmpty()) {
            LocalTime timeOfDay = time.toLocalTime();
            LocalTime sessionEndTime = timeOfDay.plusHours(SESSION_DURATION_HOURS);
            for (StudentAvailability avail : studentAvailabilities) {
                if (matchesDayOfWeek(dayOfWeek, avail.getDayOfWeek())) {
                    if (timeOfDay.compareTo(avail.getStartTime()) >= 0 && 
                        sessionEndTime.compareTo(avail.getEndTime()) <= 0) {
                        score += 30.0; // Bonus điểm cao nếu khớp với student availability
                    }
                }
            }
        }

        // Không có xung đột lịch
        if (!hasMentorConflict(time, mentor.getMentorId())) {
            score += 40.0;
        }

        // SC3: Tránh xếp nhiều lớp liên tiếp trong 1 ngày cho mentor - Soft Constraint 3
        int classesOnSameDay = countMentorClassesOnDay(mentor.getMentorId(), time.toLocalDate());
        if (classesOnSameDay >= 5) {
            score -= 50.0; // Penalty lớn nếu >= 5 lớp trong 1 ngày
        } else if (classesOnSameDay >= 3) {
            score -= 20.0; // Penalty vừa nếu >= 3 lớp
        } else if (classesOnSameDay >= 2) {
            score -= 5.0; // Penalty nhỏ nếu >= 2 lớp
        }

        int sessionsToday = countMentorSessionsOnDate(mentor.getMentorId(), time.toLocalDate());
        if (sessionsToday >= 3) score -= 30.0;

        return score;
    }

    /**
     * SC3: Đếm số lớp mentor dạy trong cùng 1 ngày - Soft Constraint 3
     */
    private int countMentorClassesOnDay(String mentorId, LocalDate date) {
        if (mentorId == null || date == null) return 0;
        
        return (int) learningSessionController.getAllLearningSessions().stream()
                .filter(s -> {
                    LearningPlan plan = learningPlanController.getLearningPlanById(s.getPlanId());
                    return plan != null && plan.getMentorId().equals(mentorId);
                })
                .filter(s -> s.getSessionStatus() == LearningSession.SessionStatus.SCHEDULED)
                .filter(s -> s.getScheduledTime().toLocalDate().equals(date))
                .count();
    }

    private int countMentorSessionsOnDate(String mentorId, LocalDate date) {
        return (int) learningSessionController.getAllLearningSessions().stream()
                .filter(s -> {
                    LearningPlan p = learningPlanController.getLearningPlanById(s.getPlanId());
                    return p != null && p.getMentorId().equals(mentorId);
                })
                .filter(s -> s.getScheduledTime().toLocalDate().equals(date))
                .count();
    }

    /**
     * Chuyển đổi DayOfWeek (Java) sang DayOfWeeks (enum trong model)
     */
    private boolean matchesDayOfWeek(DayOfWeek javaDayOfWeek, StudentPreference.DayOfWeeks preferenceDay) {
        switch (javaDayOfWeek) {
            case MONDAY: return preferenceDay == StudentPreference.DayOfWeeks.MONDAY;
            case TUESDAY: return preferenceDay == StudentPreference.DayOfWeeks.TUESDAY;
            case WEDNESDAY: return preferenceDay == StudentPreference.DayOfWeeks.WEDNESDAY;
            case THURSDAY: return preferenceDay == StudentPreference.DayOfWeeks.THURSDAY;
            case FRIDAY: return preferenceDay == StudentPreference.DayOfWeeks.FRIDAY;
            case SATURDAY: return preferenceDay == StudentPreference.DayOfWeeks.SATURDAY;
            case SUNDAY: return preferenceDay == StudentPreference.DayOfWeeks.SUNDAY;
            default: return false;
        }
    }

    /**
     * Chuyển đổi DayOfWeek (Java) sang DayOfWeeks (enum trong MentorAvailability)
     */
    private boolean matchesDayOfWeek(DayOfWeek javaDayOfWeek, MentorAvailability.DayOfWeeks availabilityDay) {
        switch (javaDayOfWeek) {
            case MONDAY: return availabilityDay == MentorAvailability.DayOfWeeks.MONDAY;
            case TUESDAY: return availabilityDay == MentorAvailability.DayOfWeeks.TUESDAY;
            case WEDNESDAY: return availabilityDay == MentorAvailability.DayOfWeeks.WEDNESDAY;
            case THURSDAY: return availabilityDay == MentorAvailability.DayOfWeeks.THURSDAY;
            case FRIDAY: return availabilityDay == MentorAvailability.DayOfWeeks.FRIDAY;
            case SATURDAY: return availabilityDay == MentorAvailability.DayOfWeeks.SATURDAY;
            case SUNDAY: return availabilityDay == MentorAvailability.DayOfWeeks.SUNDAY;
            default: return false;
        }
    }

    /**
     * Chuyển đổi DayOfWeek (Java) sang DayOfWeeks (enum trong StudentAvailability)
     */
    private boolean matchesDayOfWeek(DayOfWeek javaDayOfWeek, StudentAvailability.DayOfWeeks availabilityDay) {
        switch (javaDayOfWeek) {
            case MONDAY: return availabilityDay == StudentAvailability.DayOfWeeks.MONDAY;
            case TUESDAY: return availabilityDay == StudentAvailability.DayOfWeeks.TUESDAY;
            case WEDNESDAY: return availabilityDay == StudentAvailability.DayOfWeeks.WEDNESDAY;
            case THURSDAY: return availabilityDay == StudentAvailability.DayOfWeeks.THURSDAY;
            case FRIDAY: return availabilityDay == StudentAvailability.DayOfWeeks.FRIDAY;
            case SATURDAY: return availabilityDay == StudentAvailability.DayOfWeeks.SATURDAY;
            case SUNDAY: return availabilityDay == StudentAvailability.DayOfWeeks.SUNDAY;
            default: return false;
        }
    }

    /**
     * Sử dụng mentorAvailabilityController để kiểm tra mentor có available tại thời điểm này không
     */
    private boolean isMentorAvailableAtTime(String mentorId, LocalDateTime time) {
        if (mentorId == null || time == null) return false;
        
        List<MentorAvailability> availabilities = mentorAvailabilityController.getAvailabilityByMentorId(mentorId);
        
        // Nếu không có availability được định nghĩa, cho phép (backward compatibility)
        if (availabilities.isEmpty()) {
            return true;
        }
        
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        LocalTime timeOfDay = time.toLocalTime();
        LocalTime sessionEndTime = timeOfDay.plusHours(SESSION_DURATION_HOURS);
        
        // Kiểm tra xem có availability nào khớp với thời gian này không
        return availabilities.stream()
                .anyMatch(avail -> 
                    matchesDayOfWeek(dayOfWeek, avail.getDayOfWeek()) &&
                    timeOfDay.compareTo(avail.getStartTime()) >= 0 &&
                    sessionEndTime.compareTo(avail.getEndTime()) <= 0
                );
    }

    /**
     * Sử dụng studentAvailabilityController để kiểm tra student có available tại thời điểm này không
     */
    private boolean isStudentAvailableAtTime(String studentId, LocalDateTime time) {
        if (studentId == null || time == null) return false;
        
        List<StudentAvailability> availabilities = studentAvailabilityController.getAvailabilityByStudentId(studentId);
        
        // Nếu không có availability được định nghĩa, cho phép (backward compatibility)
        if (availabilities.isEmpty()) {
            return true;
        }
        
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        LocalTime timeOfDay = time.toLocalTime();
        LocalTime sessionEndTime = timeOfDay.plusHours(SESSION_DURATION_HOURS);
        
        // Kiểm tra xem có availability nào khớp với thời gian này không
        return availabilities.stream()
                .anyMatch(avail -> 
                    matchesDayOfWeek(dayOfWeek, avail.getDayOfWeek()) &&
                    timeOfDay.compareTo(avail.getStartTime()) >= 0 &&
                    sessionEndTime.compareTo(avail.getEndTime()) <= 0
                );
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