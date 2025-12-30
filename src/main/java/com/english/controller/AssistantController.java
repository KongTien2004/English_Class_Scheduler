package com.english.controller;

import com.english.model.Assistant;
import com.english.service.AssistantService;

import javax.swing.*;
import java.util.List;

public class AssistantController {
    private AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    public void insertAssistant(Assistant assistant, JFrame frame) {
        if (assistantService.insertAssistant(assistant)) {
            JOptionPane.showMessageDialog(frame,
                    "Assistant has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateAssistant(Assistant assistant, JFrame frame) {
        if (assistantService.updateAssistant(assistant)) {
            JOptionPane.showMessageDialog(frame,
                    "Assistant has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteAssistant(String assistantId, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure to delete this center?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (assistantService.deleteAssistant(assistantId)) {
                JOptionPane.showMessageDialog(frame,
                        "Assistant has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Please check the information",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Assistant getAssistantById(String assistantId) {
        return assistantService.getAssistantById(assistantId);
    }

    public List<Assistant> getAllAssistants() {
        return assistantService.getAllAssistants();
    }

    public List<Assistant> getAssistantsByAddress(String assistantAddress) {
        return assistantService.getAssistantsByAddress(assistantAddress);
    }
}
