package com.english.service;

import com.english.DAO.AssistantDAO;
import com.english.model.Assistant;

import java.util.List;

public class AssistantService {
    private AssistantDAO assistantDAO;

    public AssistantService(AssistantDAO assistantDAO) {
        this.assistantDAO = assistantDAO;
    }

    public AssistantDAO getAssistantDAO() {
        return assistantDAO;
    }

    public boolean insertAssistant(Assistant assistant) {
        if (!validateAssistant(assistant)) return false;
        return assistantDAO.insertAssistant(assistant);
    }

    public boolean updateAssistant(Assistant assistant) {
        if (!validateAssistant(assistant)) return false;
        return assistantDAO.updateAssistant(assistant);
    }

    public boolean deleteAssistant(String assistantId) {
        if (assistantId == null || assistantId.trim().isEmpty()) return false;
        return assistantDAO.deleteAssistant(assistantId);
    }

    public Assistant getAssistantById(String assistantId) {
        return assistantDAO.getAssistantById(assistantId);
    }

    public List<Assistant> getAllAssistants() {
        return assistantDAO.getAllAssistants();
    }

    public List<Assistant> getAssistantsByAddress(String assistantAddress) {
        return assistantDAO.getAssistantsByAddress(assistantAddress);
    }

    private boolean validateAssistant(Assistant assistant) {
        if (assistant == null) return false;
        if (assistant.getAssistantId() == null || assistant.getAssistantId().trim().isEmpty()) return false;
        if (assistant.getAssistantName() == null || assistant.getAssistantName().trim().isEmpty()) return false;
        if (assistant.getAssistantAddress() == null || assistant.getAssistantAddress().trim().isEmpty()) return false;
        return true;
    }
}
