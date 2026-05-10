package com.gym.dao;

import com.gym.model.WorkoutPlan;
import com.gym.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDAO {
    public boolean addWorkoutPlan(int userId, String title, String description, String goal) {
        String sql = "INSERT INTO workout_plans(user_id,title,description,goal) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, goal);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in addWorkoutPlan: " + e.getMessage());
            return false;
        }
    }

    public boolean updateWorkoutPlan(int workoutId, int userId, String title, String description, String goal) {
        String sql = "UPDATE workout_plans SET title=?, description=?, goal=? WHERE workout_id=? AND user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, goal);
            ps.setInt(4, workoutId);
            ps.setInt(5, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updateWorkoutPlan: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteWorkoutPlan(int workoutId, int userId) {
        String sql = "DELETE FROM workout_plans WHERE workout_id=? AND user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workoutId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in deleteWorkoutPlan: " + e.getMessage());
            return false;
        }
    }

    public List<WorkoutPlan> getWorkoutsByUser(int userId) {
        List<WorkoutPlan> workouts = new ArrayList<>();
        String sql = "SELECT * FROM workout_plans WHERE user_id=? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                WorkoutPlan workout = new WorkoutPlan();
                workout.setWorkoutId(rs.getInt("workout_id"));
                workout.setUserId(rs.getInt("user_id"));
                workout.setTitle(rs.getString("title"));
                workout.setDescription(rs.getString("description"));
                workout.setGoal(rs.getString("goal"));
                workout.setCreatedAt(rs.getTimestamp("created_at"));
                workouts.add(workout);
            }
        } catch (Exception e) {
            System.err.println("Error in getWorkoutsByUser: " + e.getMessage());
        }
        return workouts;
    }
}
