package com.gym.dao;

import com.gym.model.Attendance;
import com.gym.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    public boolean checkIn(int userId) {
        String sql = "INSERT INTO attendance(user_id,check_in_time) VALUES (?,NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in checkIn: " + e.getMessage());
            return false;
        }
    }

    public boolean checkOutLatest(int userId) {
        String sql = """
                UPDATE attendance
                SET check_out_time = NOW()
                WHERE user_id = ? AND check_out_time IS NULL
                ORDER BY check_in_time DESC
                LIMIT 1
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in checkOutLatest: " + e.getMessage());
            return false;
        }
    }

    public List<Attendance> getAttendanceByUser(int userId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE user_id=? ORDER BY check_in_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(rs.getInt("attendance_id"));
                attendance.setUserId(rs.getInt("user_id"));
                attendance.setCheckInTime(rs.getTimestamp("check_in_time"));
                attendance.setCheckOutTime(rs.getTimestamp("check_out_time"));
                list.add(attendance);
            }
        } catch (Exception e) {
            System.err.println("Error in getAttendanceByUser: " + e.getMessage());
        }
        return list;
    }

    public int getTodayAttendanceCount() {
        String sql = "SELECT COUNT(*) AS total FROM attendance WHERE DATE(check_in_time)=CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("Error in getTodayAttendanceCount: " + e.getMessage());
        }
        return 0;
    }
}
