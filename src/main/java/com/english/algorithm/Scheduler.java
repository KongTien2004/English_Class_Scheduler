package com.english.algorithm;

import com.english.controller.*;
import com.english.model.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

        // Lọc mentor khả dụng
        List<Mentor> availableMentors = allMentors.stream()
                .filter(Mentor::isAvailable)
                .filter(m -> canTeachIELTSType(m, student.getIeltsType()))
                .filter(m -> m.getCertifiedBand() >= student.getTargetBand())
                .collect(Collectors.toList());

        if (availableMentors.isEmpty()) {
            availableMentors = allMentors.stream()
                    .filter(Mentor::isAvailable)
                    .collect(Collectors.toList());
        }

        if (availableMentors.isEmpty()) return null;

        // Bắt đầu với mentor ngẫu nhiên
        Mentor currentMentor = availableMentors.get(random.nextInt(availableMentors.size()));
        double currentScore = scoreMentor(currentMentor, student);

        int noImprovementCount = 0;

        for (int i = 0; i < MAX_ITERATIONS && noImprovementCount < NO_IMPROVEMENT_THRESHOLD; i++) {
            // Lấy các mentor láng giềng (random subset)
            List<Mentor> neighbors = getRandomNeighbors(availableMentors, currentMentor, 5);

            Mentor bestNeighbor = null;
            double bestScore = currentScore;

            // Tìm mentor tốt nhất trong các láng giềng
            for (Mentor neighbor : neighbors) {
                double score = scoreMentor(neighbor, student);
                if (score > bestScore) {
                    bestNeighbor = neighbor;
                    bestScore = score;
                }
            }

            // Di chuyển đến láng giềng tốt hơn
            if (bestNeighbor != null) {
                currentMentor = bestNeighbor;
                currentScore = bestScore;
                noImprovementCount = 0;
            } else {
                noImprovementCount++;
            }
        }

        return currentMentor;
    }

    /**
     * Tìm phòng học phù hợp cho buổi học
     */
    public Room findOptimalRoom(String centerId, LocalDateTime scheduledTime, LearningSession.SessionType sessionType) {
        if (sessionType == LearningSession.SessionType.ONLINE) {
            return null; // Không cần phòng cho online
        }

        List<Room> allRooms = roomController.getAllRooms();

        // Lọc phòng theo trung tâm và khả dụng
        List<Room> availableRooms = allRooms.stream()
                .filter(r -> r.getCenterId().equals(centerId))
                .filter(Room::isAvailable)
                .filter(r -> isRoomAvailableAtTime(r, scheduledTime))
                .collect(Collectors.toList());

        if (availableRooms.isEmpty()) return null;

        // Bắt đầu với phòng ngẫu nhiên
        Room currentRoom = availableRooms.get(random.nextInt(availableRooms.size()));
        double currentScore = scoreRoom(currentRoom, scheduledTime);

        int noImprovementCount = 0;

        for (int i = 0; i < MAX_ITERATIONS && noImprovementCount < NO_IMPROVEMENT_THRESHOLD; i++) {
            List<Room> neighbors = getRandomNeighbors(availableRooms, currentRoom, 3);

            Room bestNeighbor = null;
            double bestScore = currentScore;

            for (Room neighbor : neighbors) {
                double score = scoreRoom(neighbor, scheduledTime);
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
     * Tạo lịch học tối ưu cho learning plan
     */
    public List<ScheduleProposal> createOptimalSchedule(String planId) {
        LearningPlan plan = learningPlanController.getLearningPlanById(planId);
        if (plan == null) return Collections.emptyList();

        Student student = studentController.getStudentById(plan.getStudentId());
        Mentor mentor = mentorController.getMentorById(plan.getMentorId());

        if (student == null || mentor == null) return Collections.emptyList();

        List<ScheduleProposal> proposals = new ArrayList<>();
        LocalDate currentDate = plan.getStartDate();

        // Tạo lịch cho tất cả các buổi học
        for (int sessionNum = 1; sessionNum <= plan.getTotalSessions(); sessionNum++) {
            ScheduleProposal proposal = findOptimalTimeSlot(
                    student, mentor, currentDate, sessionNum, plan.getPlanId()
            );

            if (proposal != null) {
                proposals.add(proposal);
                currentDate = proposal.scheduledTime.toLocalDate().plusDays(1);
            }
        }

        return proposals;
    }

    /**
     * Tìm khung giờ học tối ưu
     */
    private ScheduleProposal findOptimalTimeSlot(Student student, Mentor mentor,
                                                 LocalDate startDate, int sessionNumber,
                                                 String planId) {
        // Danh sách các khung giờ có thể (8:00 - 20:00, mỗi buổi 2 tiếng)
        List<LocalDateTime> possibleTimes = generatePossibleTimeSlots(startDate);

        if (possibleTimes.isEmpty()) return null;

        // Bắt đầu với thời gian ngẫu nhiên
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
            List<LocalDateTime> neighbors = getRandomNeighbors(possibleTimes, currentTime, 5);

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
     * Đánh giá độ phù hợp của mentor với student
     */
    private double scoreMentor(Mentor mentor, Student student) {
        double score = 0.0;

        // Điểm cho việc có thể dạy loại IELTS phù hợp
        if (canTeachIELTSType(mentor, student.getIeltsType())) {
            score += 40.0;
        }

        // Điểm cho certified band
        double bandDiff = mentor.getCertifiedBand() - student.getTargetBand();
        if (bandDiff >= 0) {
            if (bandDiff <= 1.0) {
                score += 30.0; // Hoàn hảo
            } else if (bandDiff <= 2.0) {
                score += 20.0; // Tốt
            } else {
                score += 10.0; // Chấp nhận được
            }
        }

        // Điểm cho tính khả dụng
        if (mentor.isAvailable()) {
            score += 20.0;
        }

        // Điểm cho workload (số lượng learning plan hiện tại)
        int mentorWorkload = countMentorActivePlans(mentor.getMentorId());
        score += Math.max(0, 10.0 - mentorWorkload); // Ưu tiên mentor ít việc hơn

        return score;
    }

    /**
     * Đánh giá độ phù hợp của phòng học
     */
    private double scoreRoom(Room room, LocalDateTime scheduledTime) {
        double score = 0.0;

        // Điểm cho capacity
        score += Math.min(30.0, room.getCapacity() * 3.0);

        // Điểm cho tính khả dụng
        if (room.isAvailable()) {
            score += 30.0;
        }

        // Điểm cho việc không bị trùng lịch
        if (isRoomAvailableAtTime(room, scheduledTime)) {
            score += 40.0;
        }

        return score;
    }

    /**
     * Đánh giá độ phù hợp của khung giờ học
     */
    private double scoreTimeSlot(LocalDateTime time, Student student, Mentor mentor) {
        double score = 0.0;

        // Điểm cho giờ vàng (9:00 - 11:00 và 14:00 - 16:00)
        int hour = time.getHour();
        if ((hour >= 9 && hour < 11) || (hour >= 14 && hour < 16)) {
            score += 30.0;
        } else if (hour >= 8 && hour < 20) {
            score += 15.0;
        }

        // Điểm cho ngày trong tuần (tránh cuối tuần)
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            score += 30.0;
        } else {
            score += 10.0;
        }

        // Điểm cho việc không bị trùng lịch
        if (!hasConflictingSession(time, mentor.getMentorId())) {
            score += 40.0;
        }

        return score;
    }

    /**
     * Kiểm tra mentor có thể dạy loại IELTS không
     */
    private boolean canTeachIELTSType(Mentor mentor, Student.IELTSType ieltsType) {
        if (ieltsType == Student.IELTSType.GENERAL) {
            return mentor.isCanTeachGeneral();
        } else {
            return mentor.isCanTeachAcademic();
        }
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
     * Kiểm tra phòng có khả dụng tại thời điểm không
     */
    private boolean isRoomAvailableAtTime(Room room, LocalDateTime time) {
        return learningSessionController.getAllLearningSessions().stream()
                .filter(s -> s.getLocation() != null && s.getLocation().contains(room.getRoomId()))
                .noneMatch(s -> isTimeOverlap(s.getScheduledTime(), time));
    }

    /**
     * Kiểm tra có buổi học trùng lịch không
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
     * Tạo danh sách các khung giờ có thể trong 7 ngày tới
     */
    private List<LocalDateTime> generatePossibleTimeSlots(LocalDate startDate) {
        List<LocalDateTime> timeSlots = new ArrayList<>();

        for (int day = 0; day < 7; day++) {
            LocalDate date = startDate.plusDays(day);

            // Các khung giờ: 8:00, 10:00, 14:00, 16:00, 18:00
            int[] hours = {8, 10, 14, 16, 18};
            for (int hour : hours) {
                timeSlots.add(LocalDateTime.of(date, LocalTime.of(hour, 0)));
            }
        }

        return timeSlots;
    }

    /**
     * Lấy các phần tử láng giềng ngẫu nhiên
     */
    private <T> List<T> getRandomNeighbors(List<T> list, T current, int count) {
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
