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
    private final CenterController centerController;
    private final RoomController roomController;
    private final LearningPlanController learningPlanController;
    private final LearningSessionController learningSessionController;
    private final MentorAvailabilityController mentorAvailabilityController;
    private final PackageController packageController;
    private final StudentAvailabilityController studentAvailabilityController;
    private final StudentPreferenceController studentPreferenceController;

    private final Random random;
    private static final int MAX_ITERATIONS = 100;
    private static final int NO_IMPROVEMENT_THRESHOLD = 10;
    private static final int SESSION_DURATION_HOURS = 2;
    private static final int SESSIONS_PER_WEEK = 2;
    private static final int COURSE_DURATION_WEEKS = 25;

    public Scheduler(StudentController studentController,
                     MentorController mentorController,
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
        this.centerController = centerController;
        this.roomController = roomController;
        this.learningPlanController = learningPlanController;
        this.learningSessionController = learningSessionController;
        this.mentorAvailabilityController = mentorAvailabilityController;
        this.packageController = packageController;
        this.studentAvailabilityController = studentAvailabilityController;
        this.studentPreferenceController = studentPreferenceController;
        this.random = new Random();
    }

    /**
     * Tìm mentor phù hợp nhất cho sinh viên sử dụng Hill Climbing
     */
    public Mentor findOptimalMentor(String studentId) {
        Student student = studentController.getStudentById(studentId);
        if (student == null) return null;

        List<Mentor> allMentors = mentorController.getAllMentors();
        if (allMentors.isEmpty()) return null;

        Map<String, Object> criteria = new HashMap<>();
        criteria.put("ieltsType", student.getIeltsType().name());
        criteria.put("targetBand", student.getTargetBand());

        return SearchHandler.mentorHandler().findOptimalEntity(allMentors, criteria);
    }

    /**
     * Tìm phòng học phù hợp cho buổi học
     */
    public Room findOptimalRoom(String centerId, LocalDateTime scheduledTime,
                                LearningSession.SessionType sessionType, String planId) {
        if (sessionType == LearningSession.SessionType.ONLINE) {
            return null;
        }

        List<Room> allRooms = roomController.getAllRooms();

        List<Room> availableRooms = allRooms.stream()
                .filter(r -> r.getCenterId().equals(centerId))
                .filter(Room::isAvailable)
                .filter(r -> isRoomAvailableAtTime(r, scheduledTime))
                .filter(r -> hasEnoughCapacity(r, planId))
                .collect(Collectors.toList());

        if (availableRooms.isEmpty()) return null;

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
     * Tạo lịch cho lớp học sao cho lớp học phải đủ số buổi và thời lượng theo khóa học
     */
    /**
     * Tạo lịch học tối ưu cho learning plan
     */
    public List<ScheduleProposal> createOptimalSchedule(String planId) {
        LearningPlan plan = learningPlanController.getLearningPlanById(planId);
        if (plan == null) {
            System.err.println("Lỗi: Không tìm thấy learning plan: " + planId);
            return Collections.emptyList();
        }

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

        List<ScheduleProposal> proposals = new ArrayList<>();
        LocalDate currentDate = plan.getStartDate();

        int failedAttempts = 0;
        final int MAX_FAILED_ATTEMPTS = 3;

        for (int sessionNum = 1; sessionNum <= plan.getTotalSessions(); sessionNum++) {
            ScheduleProposal proposal = findOptimalTimeSlot(
                    student, mentor, currentDate, sessionNum, plan.getPlanId()
            );

            if (proposal != null && isValidSessionDuration(proposal.scheduledTime)) {
                proposals.add(proposal);
                currentDate = proposal.scheduledTime.toLocalDate().plusDays(1);
                failedAttempts = 0;
            } else {
                failedAttempts++;
                System.err.println("Cảnh báo: Không thể tạo lịch cho buổi " + sessionNum);

                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    System.err.println("Lỗi: Không thể tạo lịch sau " +
                            MAX_FAILED_ATTEMPTS + " lần thử");
                    break;
                }

                currentDate = currentDate.plusDays(1);
                sessionNum--;
            }
        }

        if (!validateScheduleCompleteness(planId, proposals)) {
            System.err.println("Cảnh báo: Lịch học không đầy đủ!");
        }

        return proposals;
    }

    /**
     * Tạo LearningSession từ ScheduleProposal với validation
     */
    public LearningSession createValidatedSession(ScheduleProposal proposal,
                                                  String sessionId,
                                                  String planId,
                                                  int sessionNumber) {
        if (!isValidSessionDuration(proposal.scheduledTime)) {
            throw new IllegalArgumentException(
                    "Buổi học không hợp lệ: thời gian phải trong 8:00-21:00 và kéo dài 2 tiếng"
            );
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


// Helper methods
    /**
     * Check thời lượng buổi học đúng 2 tiếng và trong khung giờ 8:00-21:00
     */
    private boolean isValidSessionDuration(LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusHours(SESSION_DURATION_HOURS);

        if (startTime.getHour() < 8) {
            return false;
        }

        if (endTime.getHour() > 21 || (endTime.getHour() == 21 && endTime.getMinute() > 0)) {
            return false;
        }

        return true;
    }

    /**
     * Check số buổi học đủ theo khóa học
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
     * Check lịch học có đủ số buổi theo yêu cầu không
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
     * Tìm khung giờ học tối ưu
     */
    private ScheduleProposal findOptimalTimeSlot(Student student, Mentor mentor,
                                                 LocalDate startDate, int sessionNumber,
                                                 String planId) {
        List<LocalDateTime> possibleTimes = generatePossibleTimeSlots(startDate);

        if (possibleTimes.isEmpty()) return null;

        LocalDateTime currentTime = possibleTimes.get(random.nextInt(possibleTimes.size()));
        double currentScore = scoreTimeSlot(currentTime, student, mentor);

        ScheduleProposal currentProposal = new ScheduleProposal(
                currentTime,
                LearningSession.SessionType.OFFLINE, // Mặc định offline, có thể tùy chỉnh
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

    /**
     * Kiểm tra xem lớp học có sức chứa vượt quá 20 người không?
     * (Gồm giáo viên, học viên, trợ giảng)
     */
    private boolean hasEnoughCapacity(Room room, String planId) {
        LearningPlan plan = learningPlanController.getLearningPlanById(planId);
        if (plan == null) return false;

        int studentCount = 1;
        int mentorCount = 1;
        int totalMembers = studentCount + mentorCount;

        return totalMembers <= room.getCapacity();
    }

    /**
     * Đánh giá phòng học có sức chứa lý tưởng hay không?
     */
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
            int studentCount = 1;
            int mentorCount = 1;
//            int assistantCount = 0;
            int totalPeople = studentCount + mentorCount; //+ assistantCount

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
            } else {
                score += 0.0;
            }
        }

        if (room.getCapacity() >= 10 && room.getCapacity() <= 15) {
            score += 20.0;
        } else if (room.getCapacity() >= 15 && room.getCapacity() <= 20) {
            score += 15.0;
        } else if (room.getCapacity() < 10) {
            score += 10.0;
        } else {
            score += 5.0;
        }

        return score;
    }

    /**
     * Đánh giá độ phù hợp của khung giờ học
     */
    private double scoreTimeSlot(LocalDateTime time, Student student, Mentor mentor) {
        double score = 0.0;

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
            score += 10.0;
        }

        if (!hasConflictingSession(time, mentor.getMentorId())) {
            score += 40.0;
        }

        return score;
    }

    /**
     * Đếm số learning plan đang hoạt động của mentor
     */
    private int countMentorActivePlans(String mentorId) {
        return (int) learningPlanController.getAllLearningPlans().stream()
                .filter(p -> p.getMentorId().equals(mentorId))
                .filter(p -> p.getPlanStatus() == LearningPlan.PlanStatus.ACTIVE)
                .count();
    }

    /**
     * Kiểm tra các lớp học có bị trùng phòng học không
     */
    private boolean isRoomAvailableAtTime(Room room, LocalDateTime time) {
        return learningSessionController.getAllLearningSessions().stream()
                .filter(s -> s.getLocation() != null && s.getLocation().contains(room.getRoomId()))
                .noneMatch(s -> isTimeOverlap(s.getScheduledTime(), time));
    }

    /**
     * Kiểm tra giáo viên dạy có dạy cùng lúc 2 lớp không
     */
    private boolean hasConflictingSession(LocalDateTime time, String mentorId) {
        return learningSessionController.getAllLearningSessions().stream()
                .filter(s -> {
                    LearningPlan plan = learningPlanController.getLearningPlanById(s.getPlanId());
                    return plan != null && plan.getMentorId().equals(mentorId);
                })
                .anyMatch(s -> isTimeOverlap(s.getScheduledTime(), time));
    }

    /**
     * Kiểm tra 2 khung giờ có trùng nhau không (2 tiếng/buổi)
     */
    private boolean isTimeOverlap(LocalDateTime time1, LocalDateTime time2) {
        LocalDateTime end1 = time1.plusHours(2);
        LocalDateTime end2 = time2.plusHours(2);
        return time1.isBefore(end2) && time2.isBefore(end1);
    }

    /**
     * Tạo danh sách các khung giờ 8:00 - 21:00
     */
    private List<LocalDateTime> generatePossibleTimeSlots(LocalDate startDate) {
        List<LocalDateTime> timeSlots = new ArrayList<>();
        final int START_HOUR = 8;
        final int END_HOUR = 21;

        for (int day = 0; day < 7; day++) {
            LocalDate date = startDate.plusDays(day);

            for (int hour = START_HOUR; hour <= END_HOUR - SESSION_DURATION_HOURS; hour++) {
                LocalDateTime timeSlot = LocalDateTime.of(date, LocalTime.of(hour, 0));

                if (isValidSessionDuration(timeSlot)) {
                    timeSlots.add(timeSlot);
                }
            }
        }

        return timeSlots;
    }

    /**
     * Tính tổng thời lượng khóa học (giờ)
     */
    private int calculateTotalCourseDuration(LearningPlan plan) {
        return plan.getTotalSessions() * SESSION_DURATION_HOURS;
    }

    /**
     * Lấy các phần tử láng giềng ngẫu nhiên
     */
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
