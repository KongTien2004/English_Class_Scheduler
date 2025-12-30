package com.english.service;

import com.english.DAO.MentorDAO;
import com.english.DAO.StudentDAO;
import com.english.model.Mentor;

import java.util.List;

public class MentorService {
    private MentorDAO mentorDAO;

    public MentorService(MentorDAO mentorDAO) {
        this.mentorDAO = mentorDAO;
    }

    public boolean insertMentor(Mentor mentor) {
        if (!validateMentor(mentor)) return false;
        return mentorDAO.insertMentor(mentor);
    }

    public boolean updateMentor(Mentor mentor) {
        if (!validateMentor(mentor)) return false;
        return mentorDAO.updateMentor(mentor);
    }

    public boolean deleteMentor(Mentor mentor) {
        if (!validateMentor(mentor)) return false;
        return mentorDAO.deleteMentor(mentor);
    }

    public List<Mentor> getAllMentors() {
        return mentorDAO.getAllMentors();
    }

    public Mentor getMentorById(String mentorId) {
        if (mentorId == null || mentorId.trim().isEmpty()) return null;
        return mentorDAO.getMentorById(mentorId);
    }

    public List<Mentor> getAvailableMentors() {
        return mentorDAO.getAvailableMentors();
    }

    public List<Mentor> getMentorTeachGeneral() {
        return mentorDAO.getMentorTeachGeneral();
    }

    public List<Mentor> getMentorTeachAcademic() {
        return mentorDAO.getMentorTeachAcademic();
    }

    public List<Mentor> getMentorsByAddress(String mentorAddress) {return mentorDAO.getMentorsByAddress(mentorAddress);}

    private boolean validateMentor(Mentor mentor) {
        if (mentor == null) return false;
        if (mentor.getMentorId() == null || mentor.getMentorId().trim().isEmpty()) return false;
        if (mentor.getMentorName() == null || mentor.getMentorName().trim().isEmpty()) return false;
        return true;
    }

    public int totalMentors() {
        return mentorDAO.getAllMentors().size();
    }
}
