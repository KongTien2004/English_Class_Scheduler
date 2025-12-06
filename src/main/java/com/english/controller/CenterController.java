package com.english.controller;

import com.english.model.Center;
import com.english.service.CenterService;

import javax.swing.*;
import java.util.List;

public class CenterController {
    private CenterService centerService;

    public CenterController(CenterService centerService) {
        this.centerService = centerService;
    }

    public void insertCenter(Center center, JFrame frame) {
        if (centerService.insertCenter(center)) {
            JOptionPane.showMessageDialog(frame,
                    "Center has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateCenter(Center center, JFrame frame) {
        if (centerService.updateCenter(center)) {
            JOptionPane.showMessageDialog(frame,
                    "Center has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Updating center failed!",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteCenter(String centerId, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure to delete this center?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (centerService.deleteCenter(centerId)) {
                JOptionPane.showMessageDialog(frame,
                        "Center has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Deleting center failed!",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Center getCenterById(String centerId) {
        return centerService.getCenterById(centerId);
    }

    public List<Center> getAllCenters() {
        return centerService.getAllCenters();
    }

    public List<Center> getCentersByName(String centerName) {
        return centerService.getCentersByName(centerName);
    }

    public boolean centerExists(String centerId) {
        return centerService.centerExists(centerId);
    }

    public int totalNumberOfCenters() {
        return centerService.totalNumberOfCenters();
    }

    public void showCenterDetails(Center center, JFrame frame) {
        if (center != null) {
            String info = String.format(
                    "Center ID:  %s\nName: %s\nDescription: %s",
                    center.getCenterId(),
                    center.getCenterName(),
                    center.getAddress()
            );
            JOptionPane.showMessageDialog(frame,
                    info,
                    "Center Details",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Can't find the center!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
