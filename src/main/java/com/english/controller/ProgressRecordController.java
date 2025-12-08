package com.english.controller;

import com.english.model.ProgressRecord;
import com.english.service.ProgressRecordService;

import javax.swing.*;
import java.util.List;

public class ProgressRecordController {
    private ProgressRecordService progressRecordService;

    public ProgressRecordController(ProgressRecordService progressRecordService) {
        this.progressRecordService = progressRecordService;
    }

    public void insertProgressRecord(ProgressRecord progressRecord, JFrame frame) {
        if (progressRecordService.insertProgressRecord(progressRecord)) {
            JOptionPane.showMessageDialog(frame,
                    "Progress record has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "PLease check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateProgressRecord(ProgressRecord progressRecord, JFrame frame) {
        if (progressRecordService.updateProgressRecord(progressRecord)) {
            JOptionPane.showMessageDialog(frame,
                    "Progress record has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Updating progress record failed!",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteProgressRecord(ProgressRecord progressRecord, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure to delete this record?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (progressRecordService.deleteProgressRecord(progressRecord)) {
                JOptionPane.showMessageDialog(frame,
                        "Progress record has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Deleting progress record failed!",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public ProgressRecord getProgressRecordById(String progressId) {
        return progressRecordService.getProgressRecordById(progressId);
    }

    public List<ProgressRecord> getAllProgressRecords() {
        return progressRecordService.getAllProgressRecords();
    }

    public int totalProgressRecords() {
        return progressRecordService.totalProgressRecords();
    }
}
