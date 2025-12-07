package com.english.controller;

import com.english.model.Package;
import com.english.service.PackageService;

import javax.swing.*;
import java.util.List;

public class PackageController {
    private PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    public void insertPackage(Package aPackage, JFrame frame) {
        if (packageService.insertPackage(aPackage)) {
            JOptionPane.showMessageDialog(frame,
                    "Package has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updatePackage(Package aPackage, JFrame frame) {
        if (packageService.updatePackage(aPackage)) {
            JOptionPane.showMessageDialog(frame,
                    "Package has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Updating package failed!",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deletePackage(Package aPackage, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure to delete this package?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (packageService.updatePackage(aPackage)) {
                JOptionPane.showMessageDialog(frame,
                        "Package has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Deleting package failed!",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public List<Package> getAllPackages() {
        return packageService.getAllPackages();
    }

    public Package getPackageById(String packageId) {
        return packageService.getPackageById(packageId);
    }

    public int totalPackages() {
        return packageService.totalPackages();
    }
}
