package com.english.service;

import com.english.DAO.PurchaseDAO;
import com.english.model.Purchase;

import java.util.List;

public class PurchaseService {
    private PurchaseDAO purchaseDAO;

    public PurchaseService(PurchaseDAO purchaseDAO) {
        this.purchaseDAO = purchaseDAO;
    }

    public boolean insertPurchase(Purchase purchase) {
        if (!validatePurchase(purchase)) return false;
        return purchaseDAO.insertPurchase(purchase);
    }

    public boolean updatePurchase(Purchase purchase) {
        if (!validatePurchase(purchase)) return false;
        return purchaseDAO.updatePurchase(purchase);
    }

    public boolean deletePurchase(Purchase purchase) {
        if (!validatePurchase(purchase)) return false;
        return purchaseDAO.deletePurchase(purchase);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseDAO.getAllPurchases();
    }

    public Purchase getPurchaseById(String purchaseId) {
        return purchaseDAO.getPurchaseById(purchaseId);
    }

    private boolean validatePurchase(Purchase purchase) {
        if (purchase == null) return false;
        if (purchase.getPurchaseId() == null || purchase.getPurchaseId().trim().isEmpty()) return false;
        if (purchase.getStudentId() == null || purchase.getStudentId().trim().isEmpty()) return false;
        if (purchase.getPackageId() == null || purchase.getPackageId().trim().isEmpty()) return false;

        return true;
    }

    public double totalAmountPaid() {
        List<Purchase> purchases = purchaseDAO.getAllPurchases();
        if (purchases == null || purchases.isEmpty()) return 0.0;

        double total = 0.0;
        for (Purchase purchase : purchases) {
            if (purchase.getAmountPaid() >= 0) total += purchase.getAmountPaid();
        }

        return total;
    }
}
