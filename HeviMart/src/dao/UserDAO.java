/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.User;
import util.koneksi; // Sesuaikan dengan nama kelas koneksi Anda
import util.PasswordUtil; // Untuk hashing password
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        // Query disesuaikan agar tidak mengambil password_hash
        String sql = "SELECT id_pengguna, nama_lengkap, username, peran, email, telepon, aktif FROM PENGGUNA ORDER BY id_pengguna ASC";
        try (Connection conn = koneksi.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setIdPengguna(rs.getInt("id_pengguna"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setNamaPengguna(rs.getString("username")); // Ambil dari kolom 'username'
                user.setPeran(rs.getString("peran"));
                user.setEmail(rs.getString("email"));
                user.setTelepon(rs.getString("telepon"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Metode untuk mencari pengguna berdasarkan keyword (nama_lengkap atau username)
    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id_pengguna, nama_lengkap, username, peran, email, telepon, aktif FROM PENGGUNA WHERE nama_lengkap LIKE ? OR username LIKE ? ORDER BY id_pengguna ASC";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setIdPengguna(rs.getInt("id_pengguna"));
                    user.setNamaLengkap(rs.getString("nama_lengkap"));
                    user.setNamaPengguna(rs.getString("username"));
                    user.setPeran(rs.getString("peran"));
                    user.setEmail(rs.getString("email"));
                    user.setTelepon(rs.getString("telepon"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Metode untuk mengambil pengguna berdasarkan ID
    public User getUserById(int id) {
        User user = null;
        String sql = "SELECT id_pengguna, nama_lengkap, username, peran, email, telepon, aktif FROM PENGGUNA WHERE id_pengguna = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setIdPengguna(rs.getInt("id_pengguna"));
                    user.setNamaLengkap(rs.getString("nama_lengkap"));
                    user.setNamaPengguna(rs.getString("username"));
                    user.setPeran(rs.getString("peran"));
                    user.setEmail(rs.getString("email"));
                    user.setTelepon(rs.getString("telepon"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // Metode untuk menambah pengguna baru
    public boolean addUser(User user, String plainPassword) {
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        // Pastikan urutan kolom sesuai dengan INSERT di database
        String sql = "INSERT INTO PENGGUNA (nama_lengkap, username, kata_sandi, peran, email, telepon, aktif, dibuat_pada, diperbarui_pada) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNamaLengkap());
            pstmt.setString(2, user.getNamaPengguna()); // Simpan username ke kolom 'username'
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, user.getPeran());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getTelepon());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metode untuk memperbarui data pengguna (kecuali password)
    public boolean updateUser(User user) {
        // Asumsi username tidak diubah, jika diubah perlu validasi unique
        String sql = "UPDATE PENGGUNA SET nama_lengkap = ?, peran = ?, email = ?, telepon = ?, aktif = ?, diperbarui_pada = NOW() WHERE id_pengguna = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNamaLengkap());
            pstmt.setString(2, user.getPeran());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getTelepon());
            pstmt.setInt(6, user.getIdPengguna());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metode untuk memperbarui password pengguna
    public boolean updateUserPassword(int userId, String hashedPassword) {
        String sql = "UPDATE PENGGUNA SET kata_sandi = ?, diperbarui_pada = NOW() WHERE id_pengguna = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metode untuk menghapus pengguna
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM PENGGUNA WHERE id_pengguna = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
