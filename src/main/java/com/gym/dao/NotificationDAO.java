package com.gym.dao;

import com.gym.model.GymNotification;
import com.gym.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public boolean addNotification(int userId, String message, String type) {
        String sql = "INSERT INTO notifications(user_id,message,type) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.setString(3, type);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in addNotification: " + e.getMessage());
            return false;
        }
    }

    public List<GymNotification> getNotificationsByUser(int userId) {
        List<GymNotification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id=? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GymNotification n = new GymNotification();
                n.setNotificationId(rs.getInt("notification_id"));
                n.setUserId(rs.getInt("user_id"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setRead(rs.getBoolean("is_read"));
                n.setCreatedAt(rs.getTimestamp("created_at"));
                notifications.add(n);
            }
        } catch (Exception e) {
            System.err.println("Error in getNotificationsByUser: " + e.getMessage());
        }
        return notifications;
    }
}
