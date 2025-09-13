package com.quiz.dao;

import com.quiz.database.DatabaseConnection;
import com.quiz.model.Role;
import com.quiz.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho User
 */
public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Đăng nhập
    public User login(String username, String password) {
        String sql = "SELECT u.*, r.name as roleName FROM Users u " +
                    "JOIN Roles r ON u.roleId = r.id " +
                    "WHERE u.username = ? AND u.password = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRoleId(rs.getInt("roleId"));
                user.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                
                if (rs.getTimestamp("lastLogin") != null) {
                    user.setLastLogin(rs.getTimestamp("lastLogin").toLocalDateTime());
                }
                
                Role role = new Role();
                role.setId(rs.getInt("roleId"));
                role.setName(rs.getString("roleName"));
                user.setRole(role);
                
                // Cập nhật lastLogin
                updateLastLogin(user.getId());
                
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật thời gian đăng nhập cuối
    private void updateLastLogin(int userId) {
        String sql = "UPDATE Users SET lastLogin = GETDATE() WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.name as roleName FROM Users u " +
                    "JOIN Roles r ON u.roleId = r.id " +
                    "ORDER BY u.createdAt DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRoleId(rs.getInt("roleId"));
                user.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                
                if (rs.getTimestamp("lastLogin") != null) {
                    user.setLastLogin(rs.getTimestamp("lastLogin").toLocalDateTime());
                }
                
                Role role = new Role();
                role.setId(rs.getInt("roleId"));
                role.setName(rs.getString("roleName"));
                user.setRole(role);
                
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Kiểm tra username đã tồn tại chưa
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Kiểm tra email đã tồn tại chưa
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Thêm user mới
    public boolean addUser(User user) {
        String sql = "INSERT INTO Users (username, password, email, roleId) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getRoleId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật user
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET username = ?, password = ?, email = ?, roleId = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getRoleId());
            stmt.setInt(5, user.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa user
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả roles
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM Roles ORDER BY id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    // Lấy user theo ID
    public User getUserById(int userId) {
        String sql = "SELECT u.*, r.name as roleName FROM Users u " +
                    "JOIN Roles r ON u.roleId = r.id " +
                    "WHERE u.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRoleId(rs.getInt("roleId"));
                user.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                
                if (rs.getTimestamp("lastLogin") != null) {
                    user.setLastLogin(rs.getTimestamp("lastLogin").toLocalDateTime());
                }
                
                Role role = new Role();
                role.setId(rs.getInt("roleId"));
                role.setName(rs.getString("roleName"));
                user.setRole(role);
                
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
