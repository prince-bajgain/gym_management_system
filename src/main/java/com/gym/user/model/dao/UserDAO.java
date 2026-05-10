package com.gym.user.model.dao;

import com.gym.user.model.User;
import com.gym.utils.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements UserInterface {

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getString("role"),
                rs.getTimestamp("created_at")
        );
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (Exception e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
        }

        return users;
    }

    public List<User> searchMembers(String search, String statusFilter) {
        List<User> users = new ArrayList<>();
        String sql = """
                SELECT DISTINCT u.*
                FROM users u
                LEFT JOIN member_membership mm ON mm.user_id = u.user_id
                WHERE u.role='MEMBER'
                  AND (u.full_name LIKE ? OR u.email LIKE ?)
                  AND (? = 'ALL' OR mm.status = ?)
                ORDER BY u.created_at DESC
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String wildcard = "%" + search + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            ps.setString(3, statusFilter);
            ps.setString(4, statusFilter);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (Exception e) {
            System.err.println("Error in searchMembers: " + e.getMessage());
        }
        return users;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error in deleteUser: " + e.getMessage());
            return false;
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.err.println("Error in emailExists: " + e.getMessage());
            return false;
        }
    }

    public boolean emailExistsForOtherUser(String email, int userId) {
        String sql = "SELECT 1 FROM users WHERE email=? AND user_id <> ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Error in emailExistsForOtherUser: " + e.getMessage());
            return false;
        }
    }

    public boolean userExists(int userId) {
        String sql = "SELECT 1 FROM users WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Error in userExists: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean registerUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            return false;
        }
        String sql = "INSERT INTO users(full_name,email,password_hash,phone,role) VALUES(?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());

            String hashedPassword = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt());
            ps.setString(3, hashedPassword);
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole());

            int row = ps.executeUpdate();
            return row > 0;
        } catch (Exception e) {
            System.err.println("Error in registerUser: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User loginUser(String email, String password) {

        String sql = "SELECT * FROM users WHERE email=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password_hash");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in loginUser: " + e.getMessage());
            return null;
        }
        return null;
    }

    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (Exception e) {
            System.err.println("Error in findById: " + e.getMessage());
        }
        return null;
    }

    public boolean updateMemberByAdmin(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, phone=?, role=? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getRole());
            ps.setInt(5, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updateMemberByAdmin: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProfile(int userId, String fullName, String phone) {
        String sql = "UPDATE users SET full_name=?, phone=? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, phone);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updateProfile: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(int userId, String currentPassword, String newPassword) {
        User user = findById(userId);
        if (user == null || !BCrypt.checkpw(currentPassword, user.getPasswordHash())) {
            return false;
        }

        String sql = "UPDATE users SET password_hash=? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updatePassword: " + e.getMessage());
            return false;
        }
    }

    public int getTotalMembersCount() {
        String sql = "SELECT COUNT(*) AS total FROM users WHERE role='MEMBER'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("Error in getTotalMembersCount: " + e.getMessage());
        }
        return 0;
    }
}
