package com.english.service;

import com.english.DAO.ProgressRecordDAO;
import com.english.model.ProgressRecord;

import java.util.List;

public class ProgressRecordService {
    private ProgressRecordDAO progressRecordDAO;

    public ProgressRecordService(ProgressRecordDAO progressRecordDAO) {
        this.progressRecordDAO = progressRecordDAO;
    }

    public boolean insertProgressRecord(ProgressRecord progressRecord) {
        if (!validateRecord(progressRecord)) return false;
        return progressRecordDAO.insertProgressRecord(progressRecord);
    }

    public boolean updateProgressRecord(ProgressRecord progressRecord) {
        if (!validateRecord(progressRecord)) return false;
        return progressRecordDAO.updateProgressRecord(progressRecord);
    }

    public boolean deleteProgressRecord(ProgressRecord progressRecord) {
        if (!validateRecord(progressRecord)) return false;
        return progressRecordDAO.deleteProgressRecord(progressRecord);
    }

    private boolean validateRecord(ProgressRecord progressRecord) {
        if (progressRecord == null) return false;
        if (progressRecord.getProgressId() == null || progressRecord.getProgressId().trim().isEmpty()) return false;
        if (progressRecord.getStudentId() == null || progressRecord.getStudentId().trim().isEmpty()) return false;

        return true;
    }

    public ProgressRecord getProgressRecordById(String progressId) {
        return progressRecordDAO.getProgressRecordById(progressId);
    }

    public List<ProgressRecord> getAllProgressRecords() {
        return progressRecordDAO.getAllProgressRecords();
    }

    public int totalProgressRecords() {
        return progressRecordDAO.getAllProgressRecords().size();
    }
}
