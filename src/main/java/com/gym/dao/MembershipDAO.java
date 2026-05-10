package com.gym.dao;

import com.gym.model.MemberMembership;
import com.gym.model.MembershipPlan;
import com.gym.utils.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MembershipDAO {
    public boolean createPlan(String planName, int durationDays, double price, String description) {
        String sql = "INSERT INTO membership_plans(plan_name,duration_days,price,description) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, planName);
            ps.setInt(2, durationDays);
            ps.setDouble(3, price);
            ps.setString(4, description);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in createPlan: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePlan(int planId, String planName, int durationDays, double price, String description) {
        String sql = "UPDATE membership_plans SET plan_name=?, duration_days=?, price=?, description=? WHERE plan_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, planName);
            ps.setInt(2, durationDays);
            ps.setDouble(3, price);
            ps.setString(4, description);
            ps.setInt(5, planId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updatePlan: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePlan(int planId) {
        String sql = "DELETE FROM membership_plans WHERE plan_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, planId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in deletePlan: " + e.getMessage());
            return false;
        }
    }

    public MembershipPlan getPlanById(int planId) {
        String sql = "SELECT * FROM membership_plans WHERE plan_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, planId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MembershipPlan plan = new MembershipPlan();
                plan.setPlanId(rs.getInt("plan_id"));
                plan.setPlanName(rs.getString("plan_name"));
                plan.setDurationDays(rs.getInt("duration_days"));
                plan.setPrice(rs.getDouble("price"));
                plan.setDescription(rs.getString("description"));
                return plan;
            }
        } catch (Exception e) {
            System.err.println("Error in getPlanById: " + e.getMessage());
        }
        return null;
    }

    public List<MembershipPlan> getAllPlans() {
        List<MembershipPlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM membership_plans ORDER BY price ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MembershipPlan plan = new MembershipPlan();
                plan.setPlanId(rs.getInt("plan_id"));
                plan.setPlanName(rs.getString("plan_name"));
                plan.setDurationDays(rs.getInt("duration_days"));
                plan.setPrice(rs.getDouble("price"));
                plan.setDescription(rs.getString("description"));
                plans.add(plan);
            }
        } catch (Exception e) {
            System.err.println("Error in getAllPlans: " + e.getMessage());
        }
        return plans;
    }

    public boolean assignMembership(int userId, int planId, Date startDate, Date endDate, String status) {
        String sql = "INSERT INTO member_membership(user_id,plan_id,start_date,end_date,status) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, planId);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            ps.setString(5, status);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in assignMembership: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMembershipStatus(int membershipId, String status) {
        String sql = "UPDATE member_membership SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, membershipId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updateMembershipStatus: " + e.getMessage());
            return false;
        }
    }

    public MemberMembership getLatestMembershipByUser(int userId) {
        String sql = """
                SELECT mm.*, mp.plan_name
                FROM member_membership mm
                JOIN membership_plans mp ON mp.plan_id = mm.plan_id
                WHERE mm.user_id=?
                ORDER BY mm.end_date DESC
                LIMIT 1
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MemberMembership membership = new MemberMembership();
                membership.setId(rs.getInt("id"));
                membership.setUserId(rs.getInt("user_id"));
                membership.setPlanId(rs.getInt("plan_id"));
                membership.setPlanName(rs.getString("plan_name"));
                membership.setStartDate(rs.getDate("start_date"));
                membership.setEndDate(rs.getDate("end_date"));
                membership.setStatus(rs.getString("status"));
                return membership;
            }
        } catch (Exception e) {
            System.err.println("Error in getLatestMembershipByUser: " + e.getMessage());
        }
        return null;
    }

    public List<MemberMembership> getExpiringMemberships() {
        List<MemberMembership> memberships = new ArrayList<>();
        String sql = """
                SELECT mm.*, mp.plan_name
                FROM member_membership mm
                JOIN membership_plans mp ON mp.plan_id=mm.plan_id
                WHERE mm.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
                ORDER BY mm.end_date ASC
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MemberMembership m = new MemberMembership();
                m.setId(rs.getInt("id"));
                m.setUserId(rs.getInt("user_id"));
                m.setPlanId(rs.getInt("plan_id"));
                m.setPlanName(rs.getString("plan_name"));
                m.setStartDate(rs.getDate("start_date"));
                m.setEndDate(rs.getDate("end_date"));
                m.setStatus(rs.getString("status"));
                memberships.add(m);
            }
        } catch (Exception e) {
            System.err.println("Error in getExpiringMemberships: " + e.getMessage());
        }
        return memberships;
    }

    public int getActiveMembershipCount() {
        String sql = "SELECT COUNT(*) AS total FROM member_membership WHERE status='ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("Error in getActiveMembershipCount: " + e.getMessage());
        }
        return 0;
    }
}
