package com.gym.dao;

import com.gym.model.Payment;
import com.gym.utils.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    public boolean addPayment(int userId, double amount, Date paymentDate, String method, String status) {
        String sql = "INSERT INTO payments(user_id,amount,payment_date,payment_method,status) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDouble(2, amount);
            ps.setDate(3, paymentDate);
            ps.setString(4, method);
            ps.setString(5, status);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in addPayment: " + e.getMessage());
            return false;
        }
    }

    public List<Payment> getPaymentsByUser(int userId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE user_id=? ORDER BY payment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setUserId(rs.getInt("user_id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentDate(rs.getDate("payment_date"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setStatus(rs.getString("status"));
                payments.add(payment);
            }
        } catch (Exception e) {
            System.err.println("Error in getPaymentsByUser: " + e.getMessage());
        }
        return payments;
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(amount),0) AS total FROM payments WHERE status='PAID'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            System.err.println("Error in getTotalRevenue: " + e.getMessage());
        }
        return 0;
    }
}
