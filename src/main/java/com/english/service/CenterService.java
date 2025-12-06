package com.english.service;

import com.english.DAO.CenterDAO;
import com.english.model.Center;

import java.util.List;


public class CenterService {
    private CenterDAO centerDAO;

    public CenterService(CenterDAO centerDAO) {
        this.centerDAO = centerDAO;
    }

    public boolean insertCenter(Center center) {
        if (!validateCenter(center)) return false;
        return centerDAO.insertCenter(center);
    }

    public boolean updateCenter(Center center) {
        if (!validateCenter(center)) return false;
        return centerDAO.updateCenter(center);
    }

    public boolean deleteCenter(String centerId) {
        if (centerId == null || centerId.trim().isEmpty()) return false;
        return centerDAO.deleteCenter(centerId);
    }

    public boolean centerExists(String centerId) {
        return centerDAO.getCenterById(centerId) != null;
    }

    public Center getCenterById(String centerId) {
        if (centerId == null || centerId.trim().isEmpty()) return null;
        return centerDAO.getCenterById(centerId);
    }

    public List<Center> getAllCenters() {
        return centerDAO.getAllCenters();
    }

    public List<Center> getCentersByName(String centerName) {
        if (centerName == null || centerName.trim().isEmpty()) return getAllCenters();
        return centerDAO.getCenterByName(centerName);
    }

    public int totalNumberOfCenters() {
        return getAllCenters().size();
    }

    private boolean validateCenter(Center center) {
        if (center == null) return false;
        if (center.getCenterId() == null || center.getCenterId().trim().isEmpty()) return false;
        if (center.getCenterName() == null || center.getCenterName().trim().isEmpty()) return false;
        if (center.getAddress() == null || center.getAddress().trim().isEmpty()) return false;
        return true;
    }
}
