package com.gym.user.model.dao;

import com.gym.user.model.User;
import com.gym.utils.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO implements UserInterface{

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email=?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.err.println("Error in emailExists: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean registerUser(User user) {
        if(user.getEmail().trim().isEmpty() || user.getPassword().trim().isEmpty()){
            return false;
        }
        String sql = "INSERT INTO users(name,email,password,role) values(?,?,?,?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1,user.getName());
            ps.setString(2, user.getEmail());

            String hashedPassword = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt());
            ps.setString(3, hashedPassword);

            ps.setString(4,user.getRole());
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

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                String hashedPassword = rs.getString("password");
                if(BCrypt.checkpw(password,hashedPassword)) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            null,
                            rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error in loginUser: " + e.getMessage());
            return null;
        }
        return null;
    }
}
