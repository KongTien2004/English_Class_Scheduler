package com.english.service;

import com.english.DAO.MentorAvailabilityDAO;
import com.english.model.MentorAvailability;

public class MentorAvailabilityService {
    private MentorAvailabilityDAO mentorAvailabilityDAO;

    public MentorAvailabilityService(MentorAvailabilityDAO mentorAvailabilityDAO) {
        this.mentorAvailabilityDAO = mentorAvailabilityDAO;
    }

    public MentorAvailabilityDAO getMentorAvailabilityDAO() {
        return mentorAvailabilityDAO;
    }

    public boolean insertMentorAvailability(MentorAvailability mentorAvailability) {
        return mentorAvailabilityDAO.insertMentorAvailability(mentorAvailability);
    }

    public boolean updateMentorAvailability(MentorAvailability mentorAvailability) {
        return mentorAvailabilityDAO.updateMentorAvailability(mentorAvailability);
    }

    public boolean deleteMentorAvailability(MentorAvailability mentorAvailability) {
        return mentorAvailabilityDAO.deleteMentorAvailability(mentorAvailability);
    }
}
