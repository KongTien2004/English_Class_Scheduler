package com.english.service;

import com.english.DAO.PackageDAO;
import com.english.model.Package;

import java.util.List;

public class PackageService {
    private PackageDAO packageDAO;

    public PackageService(PackageDAO packageDAO) {
        this.packageDAO = packageDAO;
    }

    public boolean insertPackage(Package aPackage) {
        if (!validatePacakge(aPackage)) return false;
        return packageDAO.insertPackage(aPackage);
    }

    public boolean updatePackage(Package aPackage) {
        if (!validatePacakge(aPackage)) return false;
        return packageDAO.updatePackage(aPackage);
    }

    public boolean deletePackage(Package aPackage) {
        if (!validatePacakge(aPackage)) return false;
        return packageDAO.deletePackage(aPackage);
    }

    public List<Package> getAllPackages() {
        return packageDAO.getAllPackages();
    }

    public Package getPackageById(String packageId) {
        return packageDAO.getPackageById(packageId);
    }

    private boolean validatePacakge(Package aPackage) {
        if (aPackage == null) return false;
        if (aPackage.getPackageId() == null || aPackage.getPackageId().trim().isEmpty()) return false;
        if (aPackage.getPackageName() == null || aPackage.getPackageName().trim().isEmpty()) return false;
        if (aPackage.getIeltsType() == null) return false;
        if (aPackage.getTargetBand() <= 0) return false;
        if (aPackage.getTotalSessions() <= 0) return false;
        if (aPackage.getPrice() <= 0) return false;

        return true;
    }

    public int totalPackages() {
        return packageDAO.getAllPackages().size();
    }
}
