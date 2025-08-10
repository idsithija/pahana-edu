package com.pahanaedu.dao;

import com.pahanaedu.model.User;
import com.pahanaedu.util.DbConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public User findByUsername(String username) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by username: " + username, e);
        }
        return user;
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("DB Error creating user: " + e.getMessage());
            return false;
        }
    }
}
