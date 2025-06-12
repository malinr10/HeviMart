/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Lenovo
 */

public class koneksi {
    private static Connection koneksi;

    public static Connection getKoneksi() throws SQLException {
        String url = "jdbc:mysql://localhost:3307/supermart_db";
        String user = "root";
        String password = ""; // Sesuaikan

        // Mengembalikan koneksi baru setiap kali method ini dipanggil
        return DriverManager.getConnection(url, user, password);
    }
        
}
