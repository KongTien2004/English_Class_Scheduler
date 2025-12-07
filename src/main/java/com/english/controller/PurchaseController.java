package com.english.controller;

import com.english.model.Purchase;
import com.english.service.PurchaseService;

import javax.swing.*;
import java.util.List;

public class PurchaseController {
    private PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    public void insertPurchase(Purchase purchase, JFrame frame) {
        if (purchaseService.insertPurchase(purchase)) {
            JOptionPane.showMessageDialog(frame,
                    "Student's purchase has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "There's something wrong in this purchase",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updatePurchase(Purchase purchase, JFrame frame) {
        if (purchaseService.updatePurchase(purchase)) {
            JOptionPane.showMessageDialog(frame,
                    "Student's purchase has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "There's something wrong in this purchase",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    public void deletePurchase(Purchase purchase, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure to delete this purchase?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (purchaseService.deletePurchase(purchase)) {
                JOptionPane.showMessageDialog(frame,
                        "Student's purchase has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "There's something wrong in this purchase",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    public Purchase getPurchaseById(String purchaseId) {
        return purchaseService.getPurchaseById(purchaseId);
    }

    public double totalAmountPaid() {
        return purchaseService.totalAmountPaid();
    }
}
