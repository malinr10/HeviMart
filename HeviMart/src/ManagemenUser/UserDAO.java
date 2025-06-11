/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ManagemenUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.koneksi;
import util.PasswordUtil; 

/**
 *
 * @author mdr
 */
public class UserDAO {
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id_pengguna, nama_lengkap, nama_pengguna, peran, email, telepon, kata_sandi, aktif FROM PENGGUNA ORDER BY id_pengguna ASC";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id_pengguna"),
                    rs.getString("nama_lengkap"),
                    rs.getString("nama_pengguna"),
                    rs.getString("peran"),
                    rs.getString("email"),
                    rs.getString("telepon"),
                    rs.getString("kata_sandi"),
                    rs.getBoolean("aktif")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            // Anda bisa tambahkan logging atau throw exception yang lebih spesifik
        }
        return users;
    }
    
    
    public boolean isUsernameOrEmailExists(String username, String email, int excludeUserId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PENGGUNA WHERE (nama_pengguna = ? OR email = ?) AND id_pengguna != ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setInt(3, excludeUserId); // Gunakan -1 jika untuk user baru (tidak ada yang dikecualikan)
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id_pengguna, nama_lengkap, nama_pengguna, peran, email, telepon, kata_sandi, aktif FROM PENGGUNA " +
                     "WHERE nama_lengkap LIKE ? OR nama_pengguna LIKE ? OR email LIKE ? OR telepon LIKE ? OR peran LIKE ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            pstmt.setString(3, searchKeyword);
            pstmt.setString(4, searchKeyword);
            pstmt.setString(5, searchKeyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getInt("id_pengguna"),
                        rs.getString("nama_lengkap"),
                        rs.getString("nama_pengguna"),
                        rs.getString("peran"),
                        rs.getString("email"),
                        rs.getString("telepon"),
                        rs.getString("kata_sandi"),
                        rs.getBoolean("aktif")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        return users;
    }

    public User getUserById(int id) {
        String sql = "SELECT id_pengguna, nama_lengkap, nama_pengguna, peran, email, telepon, kata_sandi, aktif FROM PENGGUNA WHERE id_pengguna = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id_pengguna"),
                        rs.getString("nama_lengkap"),
                        rs.getString("nama_pengguna"),
                        rs.getString("peran"),
                        rs.getString("email"),
                        rs.getString("telepon"),
                        rs.getString("kata_sandi"),
                        rs.getBoolean("aktif")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean addUser(User user, String plainPassword) {
        String sql = "INSERT INTO PENGGUNA (nama_lengkap, nama_pengguna, peran, email, telepon, kata_sandi, aktif) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Cek duplikasi sebelum insert
            if (isUsernameOrEmailExists(user.getNamaPengguna(), user.getEmail(), -1)) {
                System.err.println("Username or email already exists for new user.");
                return false;
            }

            pstmt.setString(1, user.getNamaLengkap());
            pstmt.setString(2, user.getNamaPengguna());
            pstmt.setString(3, user.getPeran());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getTelepon());
            pstmt.setString(6, PasswordUtil.hashPassword(plainPassword));
            pstmt.setBoolean(7, user.isAktif());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE PENGGUNA SET nama_lengkap = ?, nama_pengguna = ?, peran = ?, email = ?, telepon = ?, aktif = ? WHERE id_pengguna = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Cek duplikasi username/email untuk user yang sedang diedit
            if (isUsernameOrEmailExists(user.getNamaPengguna(), user.getEmail(), user.getIdPengguna())) {
                System.err.println("Username or email already exists for other user.");
                return false;
            }

            pstmt.setString(1, user.getNamaLengkap());
            pstmt.setString(2, user.getNamaPengguna());
            pstmt.setString(3, user.getPeran());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getTelepon());
            pstmt.setBoolean(6, user.isAktif());
            pstmt.setInt(7, user.getIdPengguna());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(int userId, String newPlainPassword) {
        String sql = "UPDATE PENGGUNA SET kata_sandi = ? WHERE id_pengguna = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, PasswordUtil.hashPassword(newPlainPassword));
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    // Di dalam kelas UserDAO.java
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM PENGGUNA WHERE id_pengguna = ?";
        try (Connection conn = util.koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace(); // Penting untuk melihat detail error database
            return false;
        }
    }
}
