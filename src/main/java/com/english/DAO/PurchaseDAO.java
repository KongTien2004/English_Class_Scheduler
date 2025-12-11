package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.Purchase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {
    public boolean insertPurchase(Purchase purchase) {
        String query = "INSERT INTO purchase (purchase_id, student_id, package_id, purchased_date, amount_paid) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, purchase.getPurchaseId());
            statement.setString(2, purchase.getStudentId());
            statement.setString(3, purchase.getPackageId());
            statement.setDate(4, java.sql.Date.valueOf(purchase.getPurchasedDate()));
            statement.setDouble(5, purchase.getAmountPaid());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePurchase(Purchase purchase) {
        String query = "UPDATE purchase SET student_id=?, package_id=?, purchased_date=?, amount_paid=? WHERE purchase_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, purchase.getStudentId());
            statement.setString(2, purchase.getPackageId());
            statement.setDate(3, java.sql.Date.valueOf(purchase.getPurchasedDate()));
            statement.setDouble(4, purchase.getAmountPaid());
            statement.setString(5, purchase.getPurchaseId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePurchase(Purchase purchase) {
        String query = "DELETE FROM purchase WHERE purchase_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, purchase.getPurchaseId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Purchase getPurchaseById(String purchaseId) {
        String query = "SELECT * FROM purchase WHERE purchase_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, purchaseId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Purchase(
                    rs.getString("purchase_id"),
                    rs.getString("student_id"),
                    rs.getString("package_id"),
                    rs.getDate("purchased_date").toLocalDate(),
                    rs.getDouble("amount_paid")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Purchase> getAllPurchases() {
        String query = "SELECT * FROM purchase";
        List<Purchase> purchases = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                purchases.add(new Purchase(
                    rs.getString("purchase_id"),
                    rs.getString("student_id"),
                    rs.getString("package_id"),
                    rs.getDate("purchased_date").toLocalDate(),
                    rs.getDouble("amount_paid")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return purchases;
    }
}
