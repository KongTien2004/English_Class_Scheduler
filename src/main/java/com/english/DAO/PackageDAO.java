package com.english.DAO;

import com.english.database.DBConnect;
import com.english.model.Package;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageDAO {
    public boolean insertPackage(Package aPackage) {
        String query = "INSERT INTO package (package_id, package_name, ielts_type, target_band, total_sessions, price, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, aPackage.getPackageId());
            statement.setString(2, aPackage.getPackageName());
            statement.setString(3, aPackage.getIeltsType().name());
            statement.setDouble(4, aPackage.getTargetBand());
            statement.setInt(5, aPackage.getTotalSessions());
            statement.setDouble(6, aPackage.getPrice());
            statement.setBoolean(7, aPackage.isActive());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePackage(Package aPackage) {
        String query = "UPDATE package SET package_name=?, ielts_type=?, target_band=?, total_sessions=?, price=?, is_active=? Where package_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, aPackage.getPackageName());
            statement.setString(2, aPackage.getIeltsType().name());
            statement.setDouble(3, aPackage.getTargetBand());
            statement.setInt(4, aPackage.getTotalSessions());
            statement.setDouble(5, aPackage.getPrice());
            statement.setBoolean(6, aPackage.isActive());
            statement.setString(7, aPackage.getPackageId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePackage(Package aPackage) {
        String query = "DELETE FROM package WHERE package_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, aPackage.getPackageId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Package> getAllPackages() {
        String query = "SELECT * FROM package";
        List<Package> packages = new ArrayList<>();

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                packages.add(new Package(
                    rs.getString("package_id"),
                    rs.getString("package_name"),
                    Package.IELTSType.valueOf(rs.getString("ielts_type")),
                    rs.getDouble("target_band"),
                    rs.getInt("total_sessions"),
                    rs.getDouble("price"),
                    rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return packages;
    }

    public Package getPackageById(String packageId) {
        String query = "SELECT * FROM package WHERE package_id=?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, packageId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Package(
                    rs.getString("package_id"),
                    rs.getString("package_name"),
                    Package.IELTSType.valueOf(rs.getString("ielts_type")),
                    rs.getDouble("target_band"),
                    rs.getInt("total_sessions"),
                    rs.getDouble("price"),
                    rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
