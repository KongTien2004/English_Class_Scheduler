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
 * OPTIMIZED Scheduler - Tối ưu hiệu năng với caching
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

    // Optimization parameters
    private static final int MAX_ITERATIONS = 30;
    private static final int NO_IMPROVEMENT_THRESHOLD = 5;
    private static final int NEIGHBOR_SIZE = 3;

    private Map<String, LearningPlan> planCache;
    private Map<String, Student> studentCache;
    private Map<String, Mentor> mentorCache;
    private Map<String, Center> centerCache;
    private Map<String, Room> roomCache;
    private List<LearningSession> allSessionsCache;
    private Map<String, List<MentorAvailability>> mentorAvailabilityCache;
    private Map<String, List<StudentAvailability>> studentAvailabilityCache;
    private Map<String, List<StudentPreference>> studentPreferenceCache;
    private Map<String, List<Assistant>> assistantCache;

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

        initializeCache();
    }

    /**
     * **MỚI: Tải toàn bộ data vào cache 1 lần duy nhất**
     */
    private void initializeCache() {
        System.out.println("🔄 Initializing cache...");
        long startTime = System.currentTimeMillis();

        // Cache plans
        planCache = learningPlanController.getAllLearningPlans().stream()
                .collect(Collectors.toMap(LearningPlan::getPlanId, p -> p));

        // Cache students
        studentCache = studentController.getAllStudents().stream()
                .collect(Collectors.toMap(Student::getStudentId, s -> s));

        // Cache mentors
        mentorCache = mentorController.getAllMentors().stream()
                .collect(Collectors.toMap(Mentor::getMentorId, m -> m));

        // Cache centers
        centerCache = centerController.getAllCenters().stream()
                .collect(Collectors.toMap(Center::getCenterId, c -> c));

        // Cache rooms
        roomCache = roomController.getAllRooms().stream()
                .collect(Collectors.toMap(Room::getRoomId, r -> r));

        // Cache sessions
        allSessionsCache = learningSessionController.getAllLearningSessions();

        // Cache mentor availability
        mentorAvailabilityCache = new HashMap<>();
        for (Mentor mentor : mentorCache.values()) {
            mentorAvailabilityCache.put(mentor.getMentorId(),
                    mentorAvailabilityController.getAvailabilityByMentorId(mentor.getMentorId()));
        }

        // Cache student availability
        studentAvailabilityCache = new HashMap<>();
        for (Student student : studentCache.values()) {
            studentAvailabilityCache.put(student.getStudentId(),
                    studentAvailabilityController.getAvailabilityByStudentId(student.getStudentId()));
        }

        // Cache student preferences
        studentPreferenceCache = new HashMap<>();
        for (Student student : studentCache.values()) {
            studentPreferenceCache.put(student.getStudentId(),
                    studentPreferenceController.getPreferenceByStudent(student.getStudentId()));
        }

        // Cache assistants by address
        assistantCache = assistantController.getAllAssistants().stream()
                .collect(Collectors.groupingBy(a -> a.getAssistantAddress() != null ? a.getAssistantAddress() : ""));

        long endTime = System.currentTimeMillis();
        System.out.println("✅ Cache initialized in " + (endTime - startTime) + "ms");
        System.out.println("   - Plans: " + planCache.size());
        System.out.println("   - Students: " + studentCache.size());
        System.out.println("   - Mentors: " + mentorCache.size());
        System.out.println("   - Centers: " + centerCache.size());
        System.out.println("   - Rooms: " + roomCache.size());
        System.out.println("   - Sessions: " + allSessionsCache.size());
    }

    /**
     * Tìm mentor tối ưu - SỬ DỤNG CACHE
     */
    public Mentor findOptimalMentor(String studentId) {
        Student student = studentCache.get(studentId);
        if (student == null) return null;

        List<Mentor> qualifiedMentors = mentorCache.values().stream()
                .filter(Mentor::isAvailable)
                .filter(m -> isMentorQualified(m, student))
                .collect(Collectors.toList());

        if (qualifiedMentors.isEmpty()) {
            System.err.println("❌ Không tìm thấy mentor phù hợp");
            return null;
        }

        Map<String, Object> criteria = new HashMap<>();
        criteria.put("ieltsType", student.getIeltsType().name());
        criteria.put("targetBand", student.getTargetBand());

        return SearchHandler.mentorHandler().findOptimalEntity(qualifiedMentors, criteria);
    }

    private boolean isMentorQualified(Mentor mentor, Student student) {
        if (student.getIeltsType() == Student.IELTSType.General) {
            return mentor.isCanTeachGeneral();
        } else if (student.getIeltsType() == Student.IELTSType.Academic) {
            return mentor.isCanTeachAcademic();
        }
        return false;
    }

    /**
     * **OPTIMIZED: Tìm phòng học - chỉ duyệt cache**
     */
    public Room findOptimalRoom(String centerId, LocalDateTime scheduledTime,
                                LearningSession.SessionType sessionType, String planId) {
        if (sessionType == LearningSession.SessionType.Online) {
            return null;
        }

        if (!centerCache.containsKey(centerId)) {
            System.err.println("❌ Center không tồn tại: " + centerId);
            return null;
        }

        if (!isWithinOperatingHours(scheduledTime)) {
            System.err.println("❌ Ngoài giờ hoạt động");
            return null;
        }

        // Lọc rooms từ cache
        List<Room> availableRooms = roomCache.values().stream()
                .filter(r -> r.getCenterId().equals(centerId))
                .filter(Room::isAvailable)
                .filter(r -> isRoomAvailableAtTime(r, scheduledTime))
                .filter(r -> hasEnoughCapacity(r, planId))
                .collect(Collectors.toList());

        if (availableRooms.isEmpty()) {
            return null;
        }

        // **GIẢM ITERATIONS để chạy nhanh hơn**
        Room currentRoom = availableRooms.get(random.nextInt(availableRooms.size()));
        double currentScore = scoreRoom(currentRoom, scheduledTime, planId);
        int noImprovementCount = 0;

        for (int i = 0; i < MAX_ITERATIONS && noImprovementCount < NO_IMPROVEMENT_THRESHOLD; i++) {
            List<Room> neighbors = getRandomNeighbor(availableRooms, currentRoom, NEIGHBOR_SIZE);

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
     * **OPTIMIZED: Tạo lịch học - sử dụng cache và giảm iterations**
     */
    public List<ScheduleProposal> createOptimalSchedule(String planId) {
        try {
            LearningPlan plan = planCache.get(planId);
            if (plan == null) {
                System.err.println("❌ Không tìm thấy plan: " + planId);
                return Collections.emptyList();
            }

            if (!hasValidSessionCount(plan)) {
                System.err.println("❌ Số buổi học không hợp lệ");
                return Collections.emptyList();
            }

            Student student = studentCache.get(plan.getStudentId());
            Mentor mentor = mentorCache.get(plan.getMentorId());

            if (student == null || mentor == null) {
                System.err.println("❌ Không tìm thấy student hoặc mentor");
                return Collections.emptyList();
            }

            if (!isMentorQualified(mentor, student)) {
                System.err.println("❌ Mentor không đủ chuyên môn");
                return Collections.emptyList();
            }

            List<ScheduleProposal> proposals = new ArrayList<>();
            LocalDate currentDate = plan.getStartDate();

            int sessionCount = 0;
            int sessionsThisWeek = 0;
            int maxWeeks = (plan.getTotalSessions() / SESSIONS_PER_WEEK) + 2;
            int weeksSearched = 0;

            System.out.println("🔍 Bắt đầu tạo lịch cho " + plan.getTotalSessions() + " buổi học...");

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

                    // Log tiến độ
                    if (sessionCount % 5 == 0) {
                        System.out.println("   ✓ Đã tạo " + sessionCount + "/" + plan.getTotalSessions() + " buổi");
                    }
                } else {
                    currentDate = currentDate.plusDays(1);
                }

                if (currentDate.isAfter(plan.getStartDate().plusWeeks(maxWeeks * 2))) {
                    System.err.println("⚠️ Vượt quá thời gian tìm kiếm");
                    break;
                }
            }

            System.out.println("✅ Hoàn thành: " + proposals.size() + "/" + plan.getTotalSessions() + " buổi");
            return proposals;

        } catch (Exception e) {
            System.err.println("💥 Lỗi: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * **OPTIMIZED: Check hard constraints - dùng cache**
     */
    private boolean validateHardConstraints(ScheduleProposal proposal, String mentorId) {
        if (proposal == null || mentorId == null || mentorId.trim().isEmpty()) {
            return false;
        }

        if (hasMentorConflict(proposal.scheduledTime, mentorId)) {
            return false;
        }

        if (!isWithinOperatingHours(proposal.scheduledTime)) {
            return false;
        }

        if (!isValidSessionDuration(proposal.scheduledTime)) {
            return false;
        }

        return true;
    }

    private boolean isWithinOperatingHours(LocalDateTime time) {
        LocalTime startTime = time.toLocalTime();
        LocalTime endTime = time.plusHours(SESSION_DURATION_HOURS).toLocalTime();
        return !startTime.isBefore(CENTER_OPEN_TIME) && !endTime.isAfter(CENTER_CLOSE_TIME);
    }

    private boolean isValidSessionDuration(LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusHours(SESSION_DURATION_HOURS);
        if (startTime.toLocalTime().isBefore(CENTER_OPEN_TIME)) return false;
        if (endTime.toLocalTime().isAfter(CENTER_CLOSE_TIME)) return false;
        return true;
    }

    private boolean hasValidSessionCount(LearningPlan plan) {
        return plan != null && plan.getTotalSessions() > 0 &&
                plan.getRemainingSessions() >= 0 &&
                plan.getRemainingSessions() <= plan.getTotalSessions();
    }

    /**
     * **OPTIMIZED: Check capacity - dùng cache**
     */
    private boolean hasEnoughCapacity(Room room, String planId) {
        LearningPlan plan = planCache.get(planId);
        if (plan == null) return false;

        int totalPeople = 2 + findSuitableAssistantCount(plan); // student + mentor + assistant
        return totalPeople <= MAX_ROOM_CAPACITY && totalPeople <= room.getCapacity();
    }

    private int findSuitableAssistantCount(LearningPlan plan) {
        Student student = studentCache.get(plan.getStudentId());
        if (student == null) return 0;

        long suitable = assistantCache.values().stream()
                .flatMap(List::stream)
                .filter(Assistant::isAvailable)
                .filter(a -> {
                    if (student.getIeltsType() == Student.IELTSType.General) {
                        return a.isCanSupportGeneral();
                    } else if (student.getIeltsType() == Student.IELTSType.Academic) {
                        return a.isCanSupportAcademic();
                    }
                    return false;
                })
                .count();

        return suitable > 0 ? 1 : 0;
    }

    /**
     * **OPTIMIZED: Check room availability - dùng cache**
     */
    private boolean isRoomAvailableAtTime(Room room, LocalDateTime time) {
        LocalDateTime endTime = time.plusHours(SESSION_DURATION_HOURS);

        return allSessionsCache.stream()
                .filter(s -> s.getLocation() != null && s.getLocation().contains(room.getRoomId()))
                .filter(s -> s.getSessionStatus() == LearningSession.SessionStatus.scheduled)
                .noneMatch(s -> {
                    LocalDateTime sessionEnd = s.getScheduledTime().plusHours(SESSION_DURATION_HOURS);
                    return time.isBefore(sessionEnd) && s.getScheduledTime().isBefore(endTime);
                });
    }

    /**
     * **OPTIMIZED: Check mentor conflict - dùng cache**
     */
    private boolean hasMentorConflict(LocalDateTime time, String mentorId) {
        LocalDateTime endTime = time.plusHours(SESSION_DURATION_HOURS);

        return allSessionsCache.stream()
                .filter(s -> {
                    LearningPlan plan = planCache.get(s.getPlanId());
                    return plan != null && plan.getMentorId().equals(mentorId);
                })
                .filter(s -> s.getSessionStatus() == LearningSession.SessionStatus.scheduled)
                .anyMatch(s -> {
                    LocalDateTime sessionEnd = s.getScheduledTime().plusHours(SESSION_DURATION_HOURS);
                    return time.isBefore(sessionEnd) && s.getScheduledTime().isBefore(endTime);
                });
    }

    /**
     * Tạo validated session
     */
    public LearningSession createValidatedSession(ScheduleProposal proposal,
                                                  String sessionId,
                                                  String planId,
                                                  int sessionNumber) {
        if (!isValidSessionDuration(proposal.scheduledTime)) {
            throw new IllegalArgumentException("Vi phạm Hard Constraint: thời lượng không hợp lệ");
        }

        LearningPlan plan = planCache.get(planId);
        if (plan != null) {
            if (!validateHardConstraints(proposal, plan.getMentorId())) {
                throw new IllegalArgumentException("Vi phạm Hard Constraints");
            }
        }

        LocalDateTime endTime = proposal.scheduledTime.plusHours(SESSION_DURATION_HOURS);

        LearningSession session = new LearningSession();
        session.setSessionId(sessionId);
        session.setPlanId(planId);
        session.setSessionNumber(sessionNumber);
        session.setSessionType(proposal.sessionType);
        session.setScheduledTime(proposal.scheduledTime);
        session.setStartTime(proposal.scheduledTime.toLocalTime());
        session.setEndTime(endTime.toLocalTime());
        session.setLocation(proposal.centerId);
        session.setSessionStatus(LearningSession.SessionStatus.scheduled);

        return session;
    }

    // ==================== OPTIMIZED HELPER METHODS ====================

    /**
     * **OPTIMIZED: Tìm time slot - giảm iterations**
     */
    private ScheduleProposal findOptimalTimeSlot(Student student, Mentor mentor,
                                                 LocalDate startDate, int sessionNumber,
                                                 String planId) {
        List<LocalDateTime> possibleTimes = generatePossibleTimeSlots(startDate);
        if (possibleTimes.isEmpty()) return null;

        // Lấy preferences từ cache
        List<StudentPreference> preferences = studentPreferenceCache.getOrDefault(student.getStudentId(), Collections.emptyList());
        String preferredCenterId = student.getPreferredCenterId();

        if (!preferences.isEmpty()) {
            String prefCenter = preferences.get(0).getPreferredCenter();
            if (prefCenter != null && centerCache.containsKey(prefCenter)) {
                preferredCenterId = prefCenter;
            }
        }

        // Validate center
        if (preferredCenterId != null && !centerCache.containsKey(preferredCenterId)) {
            List<Center> allCenters = new ArrayList<>(centerCache.values());
            if (!allCenters.isEmpty()) {
                preferredCenterId = allCenters.get(0).getCenterId();
            } else {
                preferredCenterId = null;
            }
        }

        // Lọc thời gian hợp lệ
        possibleTimes = possibleTimes.stream()
                .filter(this::isWithinOperatingHours)
                .filter(this::isValidSessionDuration)
                .filter(time -> !hasMentorConflict(time, mentor.getMentorId()))
                .filter(time -> isMentorAvailableAtTime(mentor.getMentorId(), time))
                .filter(time -> isStudentAvailableAtTime(student.getStudentId(), time))
                .collect(Collectors.toList());

        if (possibleTimes.isEmpty()) return null;

        // **Hill Climbing với iterations giảm**
        LocalDateTime currentTime = possibleTimes.get(random.nextInt(possibleTimes.size()));
        double currentScore = scoreTimeSlot(currentTime, student, mentor);

        String bestCenterId = findNearestCenterToStudent(student, preferredCenterId);
        ScheduleProposal currentProposal = new ScheduleProposal(
                currentTime,
                LearningSession.SessionType.Offline,
                bestCenterId != null ? bestCenterId : preferredCenterId,
                currentScore
        );

        int noImprovementCount = 0;

        for (int i = 0; i < MAX_ITERATIONS && noImprovementCount < NO_IMPROVEMENT_THRESHOLD; i++) {
            List<LocalDateTime> neighbors = getRandomNeighbor(possibleTimes, currentTime, NEIGHBOR_SIZE);

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
                        LearningSession.SessionType.Offline,
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

    private String findNearestCenterToStudent(Student student, String preferredCenterId) {
        if (student == null || student.getStudentAddress() == null) {
            return preferredCenterId;
        }

        List<Center> allCenters = new ArrayList<>(centerCache.values());
        if (allCenters.isEmpty()) {
            return preferredCenterId;
        }

        Center nearestCenter = null;
        int maxScore = 0;

        for (Center center : allCenters) {
            int score = 0;

            if (isCenterNearStudentAddress(student.getStudentAddress(), center)) {
                score += 100;
            }

            if (preferredCenterId != null && preferredCenterId.equals(center.getCenterId())) {
                score += 50;
            }

            if (score > maxScore) {
                maxScore = score;
                nearestCenter = center;
            }
        }

        if (nearestCenter != null && maxScore > 0) {
            return nearestCenter.getCenterId();
        }

        if (preferredCenterId != null && centerCache.containsKey(preferredCenterId)) {
            return preferredCenterId;
        }

        return allCenters.get(0).getCenterId();
    }

    /**
     * **OPTIMIZED: Score room - dùng cache**
     */
    private double scoreRoom(Room room, LocalDateTime scheduledTime, String planId) {
        double score = 0.0;

        if (room.isAvailable()) score += 20.0;
        if (isRoomAvailableAtTime(room, scheduledTime)) score += 30.0;

        LearningPlan plan = planCache.get(planId);
        if (plan != null) {
            int assistantCount = findSuitableAssistantCount(plan);
            int totalPeople = 2 + assistantCount;

            if (totalPeople <= room.getCapacity()) {
                double utilizationRate = (double) totalPeople / room.getCapacity();

                if (utilizationRate >= 0.5 && utilizationRate <= 0.8) {
                    score += 40.0;
                } else if (utilizationRate >= 0.3 && utilizationRate < 0.5) {
                    score += 25.0;
                } else if (utilizationRate > 0.8 && utilizationRate <= 0.95) {
                    score += 30.0;
                } else if (utilizationRate > 0.95) {
                    score -= 20.0;
                } else {
                    score -= 15.0;
                }
            }

            Student student = studentCache.get(plan.getStudentId());
            if (student != null) {
                Center center = centerCache.get(room.getCenterId());
                if (center != null) {
                    score += 5.0;

                    if (isCenterNearStudentAddress(student.getStudentAddress(), center)) {
                        score += 35.0;
                    } else if (student.getPreferredCenterId() != null &&
                            student.getPreferredCenterId().equals(center.getCenterId())) {
                        score += 20.0;
                    }
                }
            }
        }

        return score;
    }

    /**
     * **OPTIMIZED: Score time slot - dùng cache**
     */
    private double scoreTimeSlot(LocalDateTime time, Student student, Mentor mentor) {
        double score = 0.0;

        List<StudentPreference> preferences = studentPreferenceCache.getOrDefault(
                student.getStudentId(), Collections.emptyList());

        int hour = time.getHour();
        if ((hour >= 9 && hour < 11) || (hour >= 14 && hour < 16)) {
            score += 30.0;
        } else if (hour >= 8 && hour < 20) {
            score += 15.0;
        }

        DayOfWeek dayOfWeek = time.getDayOfWeek();
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            score += 30.0;
        } else {
            score += 5.0;
        }

        if (hour >= 18 && hour < 21) {
            score += 25.0;
        }

        // Check student preferences
        if (!preferences.isEmpty()) {
            for (StudentPreference pref : preferences) {
                if (matchesDayOfWeek(dayOfWeek, pref.getDayOfWeek())) {
                    score += 20.0;

                    LocalTime timeOfDay = time.toLocalTime();
                    if (timeOfDay.compareTo(pref.getPreferredStart()) >= 0 &&
                            timeOfDay.compareTo(pref.getPreferredEnd()) <= 0) {
                        score += 25.0;
                    }
                }
            }
        }

        // Check mentor availability
        List<MentorAvailability> mentorAvailabilities = mentorAvailabilityCache.getOrDefault(
                mentor.getMentorId(), Collections.emptyList());
        if (!mentorAvailabilities.isEmpty()) {
            LocalTime timeOfDay = time.toLocalTime();
            LocalTime sessionEndTime = timeOfDay.plusHours(SESSION_DURATION_HOURS);
            for (MentorAvailability avail : mentorAvailabilities) {
                if (matchesDayOfWeek(dayOfWeek, avail.getDayOfWeek())) {
                    if (timeOfDay.compareTo(avail.getStartTime()) >= 0 &&
                            sessionEndTime.compareTo(avail.getEndTime()) <= 0) {
                        score += 30.0;
                    }
                }
            }
        }

        // Check student availability
        List<StudentAvailability> studentAvailabilities = studentAvailabilityCache.getOrDefault(
                student.getStudentId(), Collections.emptyList());
        if (!studentAvailabilities.isEmpty()) {
            LocalTime timeOfDay = time.toLocalTime();
            LocalTime sessionEndTime = timeOfDay.plusHours(SESSION_DURATION_HOURS);
            for (StudentAvailability avail : studentAvailabilities) {
                if (matchesDayOfWeek(dayOfWeek, avail.getDayOfWeek())) {
                    if (timeOfDay.compareTo(avail.getStartTime()) >= 0 &&
                            sessionEndTime.compareTo(avail.getEndTime()) <= 0) {
                        score += 30.0;
                    }
                }
            }
        }

        if (!hasMentorConflict(time, mentor.getMentorId())) {
            score += 40.0;
        }

        int classesOnSameDay = countMentorClassesOnDay(mentor.getMentorId(), time.toLocalDate());
        if (classesOnSameDay >= 5) {
            score -= 50.0;
        } else if (classesOnSameDay >= 3) {
            score -= 20.0;
        } else if (classesOnSameDay >= 2) {
            score -= 5.0;
        }

        return score;
    }

    private int countMentorClassesOnDay(String mentorId, LocalDate date) {
        return (int) allSessionsCache.stream()
                .filter(s -> {
                    LearningPlan plan = planCache.get(s.getPlanId());
                    return plan != null && plan.getMentorId().equals(mentorId);
                })
                .filter(s -> s.getSessionStatus() == LearningSession.SessionStatus.scheduled)
                .filter(s -> s.getScheduledTime().toLocalDate().equals(date))
                .count();
    }

    private boolean matchesDayOfWeek(DayOfWeek javaDayOfWeek, StudentPreference.DayOfWeeks preferenceDay) {
        return javaDayOfWeek.name().equalsIgnoreCase(preferenceDay.name());
    }

    private boolean matchesDayOfWeek(DayOfWeek javaDayOfWeek, MentorAvailability.DayOfWeeks availabilityDay) {
        return javaDayOfWeek.name().equalsIgnoreCase(availabilityDay.name());
    }

    private boolean matchesDayOfWeek(DayOfWeek javaDayOfWeek, StudentAvailability.DayOfWeeks availabilityDay) {
        return javaDayOfWeek.name().equalsIgnoreCase(availabilityDay.name());
    }

    private boolean isMentorAvailableAtTime(String mentorId, LocalDateTime time) {
        List<MentorAvailability> availabilities = mentorAvailabilityCache.getOrDefault(
                mentorId, Collections.emptyList());

        if (availabilities.isEmpty()) return true;

        DayOfWeek dayOfWeek = time.getDayOfWeek();
        LocalTime timeOfDay = time.toLocalTime();
        LocalTime sessionEndTime = timeOfDay.plusHours(SESSION_DURATION_HOURS);

        return availabilities.stream()
                .anyMatch(avail ->
                        matchesDayOfWeek(dayOfWeek, avail.getDayOfWeek()) &&
                                timeOfDay.compareTo(avail.getStartTime()) >= 0 &&
                                sessionEndTime.compareTo(avail.getEndTime()) <= 0
                );
    }

    private boolean isStudentAvailableAtTime(String studentId, LocalDateTime time) {
        List<StudentAvailability> availabilities = studentAvailabilityCache.getOrDefault(
                studentId, Collections.emptyList());

        if (availabilities.isEmpty()) return true;

        DayOfWeek dayOfWeek = time.getDayOfWeek();
        LocalTime timeOfDay = time.toLocalTime();
        LocalTime sessionEndTime = timeOfDay.plusHours(SESSION_DURATION_HOURS);

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

            for (int hour = 8; hour <= 19; hour++) {
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

    private boolean isCenterNearStudentAddress(String studentAddress, Center center) {
        if (studentAddress == null || center == null || center.getAddress() == null) {
            return false;
        }

        String studentDistrict = extractDistrict(studentAddress);
        String centerDistrict = extractDistrict(center.getAddress());

        if (center.getCity() != null) {
            String centerCityDistrict = extractDistrict(center.getCity());
            if (studentDistrict != null && !studentDistrict.isEmpty()) {
                if (studentDistrict.equalsIgnoreCase(centerDistrict) ||
                        studentDistrict.equalsIgnoreCase(centerCityDistrict)) {
                    return true;
                }
            }
        }

        if (studentDistrict != null && centerDistrict != null &&
                !studentDistrict.isEmpty() && !centerDistrict.isEmpty()) {
            return studentDistrict.equalsIgnoreCase(centerDistrict);
        }

        return false;
    }

    private String extractDistrict(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "";
        }

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

        if (parts.length > 0) {
            return parts[parts.length - 1].trim();
        }

        return "";
    }

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