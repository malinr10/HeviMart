/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Pelaporan;

//sidebar
import Pelaporan.LaporanKeuanganForm;
import Login.Login;
import MainForm.MainMenu;
import ManagemenUser.UserManagement;
import ManajemenDiskon.DiscountManagementForm;
import ManajemenInventori.GoodsReceiptForm;
import ManajemenInventori.PurchaseOrderForm;
import ManajemenInventori.SupplierForm;
import ManajemenProduk.ProdukForm;
import Pelaporan.LaporanInventarisForm;
import Pelaporan.LaporanPenjualanForm;
import PosSistem.POSForm;
import util.koneksi;
import util.UserSession;
import Profile.Profile;

import com.sun.jdi.connect.spi.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import util.koneksi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
/**
 *
 * @author Lenovo
 */
public class LaporanInventarisForm extends javax.swing.JFrame {

    private DefaultTableModel tableModel;
    private String namaLengkap;
    private String peran;
    private int loggedInUserId;
    /**
     * Creates new form LaporanInventarisForm
     */

    
    public LaporanInventarisForm() {
        initComponents();

        this.setLocationRelativeTo(null); // Form muncul di tengah layar
        
        UserSession session = UserSession.getInstance();
        this.loggedInUserId = session.getIdPengguna(); // Ambil ID kasir dari sesi
        String namaLengkap = session.getNamaLengkap();
        String peran = session.getPeran();
        lblUsername.setText(this.namaLengkap);
        lblPeran.setText(this.peran);

        // Setup table model
        String[] columnNames = {"Kode", "Nama Barang", "Kategori", "Stok Awal", "Masuk", "Keluar", "Stok Akhir"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tabelLaporan.setModel(tableModel);

        // Muat data untuk hari ini saat form pertama kali dibuka
        loadData();
    }

    private void loadData() {
        // Clear previous data
        tableModel.setRowCount(0);

        Date tanggalMulai = dateChooserMulai.getDate();
        Date tanggalSelesai = dateChooserSelesai.getDate();
        
        if (tanggalMulai == null || tanggalSelesai == null) {
            JOptionPane.showMessageDialog(this, "Tanggal mulai dan selesai harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Format dates to yyyy-MM-dd for SQL query
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(tanggalMulai);
        String endDate = sdf.format(tanggalSelesai);
        
        

        // Query SQL yang sama seperti sebelumnya
        String sql = "SELECT p.kode_barcode, p.nama_produk, k.nama_kategori, "
                + "(p.jumlah_stok - IFNULL(moves.total_movement_in_period, 0)) AS stok_awal, "
                + "IFNULL(moves.masuk, 0) AS masuk, IFNULL(moves.keluar, 0) AS keluar, "
                + "p.jumlah_stok AS stok_akhir, p.satuan "
                + "FROM produk p "
                + "JOIN kategori k ON p.id_kategori = k.id_kategori "
                + "LEFT JOIN (SELECT id_produk, SUM(CASE WHEN tipe = 'masuk' THEN jumlah ELSE 0 END) AS masuk, "
                + "ABS(SUM(CASE WHEN tipe = 'keluar' THEN jumlah ELSE 0 END)) AS keluar, "
                + "SUM(jumlah) as total_movement_in_period "
                + "FROM pergerakan_stok WHERE CAST(tanggal AS DATE) BETWEEN ? AND ? GROUP BY id_produk) AS moves "
                + "ON p.id_produk = moves.id_produk ORDER BY p.nama_produk";

        // Gunakan kelas KoneksiDatabase yang sudah dibuat sebelumnya
        try (java.sql.Connection conn = koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, startDate);
            ps.setString(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("kode_barcode"),
                        rs.getString("nama_produk"),
                        rs.getString("nama_kategori"),
                        rs.getInt("stok_awal"),
                        rs.getInt("masuk"),
                        rs.getInt("keluar"),
                        rs.getInt("stok_akhir"),
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data laporan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Penting untuk debugging
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        dateChooserMulai = new com.toedter.calendar.JDateChooser();
        dateChooserSelesai = new com.toedter.calendar.JDateChooser();
        btnTampilkan = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelLaporan = new javax.swing.JTable();
        btnInventarisSupplier = new javax.swing.JButton();
        lblPeran = new javax.swing.JLabel();
        btnDashboard1 = new javax.swing.JButton();
        lblUsername = new javax.swing.JLabel();
        btnManajemenPengguna = new javax.swing.JButton();
        btnInventarisProduk = new javax.swing.JButton();
        btnPelaporanPenjualan = new javax.swing.JButton();
        btnLogout1 = new javax.swing.JButton();
        btnProfile = new javax.swing.JButton();
        btnManajemenProduk1 = new javax.swing.JButton();
        btnInventarisPesanan = new javax.swing.JButton();
        btnPOS = new javax.swing.JButton();
        btnPelaporanInventaris = new javax.swing.JButton();
        btnManajemenDiskon = new javax.swing.JButton();
        btnPelaporanKeuangan = new javax.swing.JButton();
        BG_LaporanInventaris = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dateChooserMulai.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel1.add(dateChooserMulai, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 322, 220, 40));

        dateChooserSelesai.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel1.add(dateChooserSelesai, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 380, 220, 40));

        btnTampilkan.setBorderPainted(false);
        btnTampilkan.setContentAreaFilled(false);
        btnTampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanActionPerformed(evt);
            }
        });
        jPanel1.add(btnTampilkan, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 470, 340, 50));

        tabelLaporan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabelLaporan);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 260, 550, 710));

        btnInventarisSupplier.setBorderPainted(false);
        btnInventarisSupplier.setContentAreaFilled(false);
        btnInventarisSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisSupplierActionPerformed(evt);
            }
        });
        jPanel1.add(btnInventarisSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 502, 110, 20));

        lblPeran.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblPeran.setForeground(new java.awt.Color(30, 41, 59));
        lblPeran.setText("peran");
        jPanel1.add(lblPeran, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 120, 20));

        btnDashboard1.setBorderPainted(false);
        btnDashboard1.setContentAreaFilled(false);
        btnDashboard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboard1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnDashboard1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 160, 30));

        lblUsername.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(30, 41, 59));
        lblUsername.setText("username");
        jPanel1.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, 120, 20));

        btnManajemenPengguna.setBorderPainted(false);
        btnManajemenPengguna.setContentAreaFilled(false);
        btnManajemenPengguna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManajemenPenggunaActionPerformed(evt);
            }
        });
        jPanel1.add(btnManajemenPengguna, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 610, 160, 60));

        btnInventarisProduk.setBorderPainted(false);
        btnInventarisProduk.setContentAreaFilled(false);
        btnInventarisProduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisProdukActionPerformed(evt);
            }
        });
        jPanel1.add(btnInventarisProduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 530, 110, 30));

        btnPelaporanPenjualan.setBorderPainted(false);
        btnPelaporanPenjualan.setContentAreaFilled(false);
        btnPelaporanPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanPenjualanActionPerformed(evt);
            }
        });
        jPanel1.add(btnPelaporanPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 720, 110, 20));

        btnLogout1.setBorderPainted(false);
        btnLogout1.setContentAreaFilled(false);
        btnLogout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogout1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnLogout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 930, 130, 40));

        btnProfile.setBorderPainted(false);
        btnProfile.setContentAreaFilled(false);
        jPanel1.add(btnProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 180, 50));

        btnManajemenProduk1.setBorderPainted(false);
        btnManajemenProduk1.setContentAreaFilled(false);
        btnManajemenProduk1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnManajemenProduk1MouseClicked(evt);
            }
        });
        btnManajemenProduk1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManajemenProduk1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnManajemenProduk1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, 160, 50));

        btnInventarisPesanan.setBorderPainted(false);
        btnInventarisPesanan.setContentAreaFilled(false);
        btnInventarisPesanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisPesananActionPerformed(evt);
            }
        });
        jPanel1.add(btnInventarisPesanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 570, 110, 30));

        btnPOS.setBorderPainted(false);
        btnPOS.setContentAreaFilled(false);
        btnPOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPOSActionPerformed(evt);
            }
        });
        jPanel1.add(btnPOS, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 258, 160, 30));

        btnPelaporanInventaris.setBorderPainted(false);
        btnPelaporanInventaris.setContentAreaFilled(false);
        btnPelaporanInventaris.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanInventarisActionPerformed(evt);
            }
        });
        jPanel1.add(btnPelaporanInventaris, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 745, 110, 20));

        btnManajemenDiskon.setBorderPainted(false);
        btnManajemenDiskon.setContentAreaFilled(false);
        btnManajemenDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManajemenDiskonActionPerformed(evt);
            }
        });
        jPanel1.add(btnManajemenDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 307, 160, 50));

        btnPelaporanKeuangan.setBorderPainted(false);
        btnPelaporanKeuangan.setContentAreaFilled(false);
        btnPelaporanKeuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanKeuanganActionPerformed(evt);
            }
        });
        jPanel1.add(btnPelaporanKeuangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 770, 70, 20));

        BG_LaporanInventaris.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Laporan Inventaris.png"))); // NOI18N
        jPanel1.add(BG_LaporanInventaris, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanActionPerformed
        // TODO add your handling code here:
        loadData();

    }//GEN-LAST:event_btnTampilkanActionPerformed
    
    private void showAccessDeniedMessage() {
        JOptionPane.showMessageDialog(this,
                "Anda tidak memiliki hak akses untuk membuka menu ini.",
                "Akses Ditolak",
                JOptionPane.WARNING_MESSAGE);
    }
    
    private void btnPOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPOSActionPerformed
        // Akses: Diizinkan untuk Kasir, Manager, Administrator. Ditolak untuk Staff Gudang.
        if (peran.equals("Staff Gudang")) {
            showAccessDeniedMessage();
            return;
        }
        new POSForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPOSActionPerformed

    private void btnManajemenDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManajemenDiskonActionPerformed
        // Akses: Diizinkan untuk Kasir, Manager, Administrator. Ditolak untuk Staff Gudang.
        if (peran.equals("Staff Gudang")) {
            showAccessDeniedMessage();
            return;
        }
        new DiscountManagementForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnManajemenDiskonActionPerformed

    private void btnManajemenProduk1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnManajemenProduk1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnManajemenProduk1MouseClicked

    private void btnManajemenProduk1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManajemenProduk1ActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new ProdukForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnManajemenProduk1ActionPerformed

    private void btnInventarisSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisSupplierActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new SupplierForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnInventarisSupplierActionPerformed

    private void btnInventarisProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisProdukActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new PurchaseOrderForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnInventarisProdukActionPerformed

    private void btnInventarisPesananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisPesananActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new GoodsReceiptForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnInventarisPesananActionPerformed

    private void btnManajemenPenggunaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManajemenPenggunaActionPerformed
        // Akses: Hanya untuk Manager dan Administrator.
        if (!peran.equals("Administrator") && !peran.equals("Manager")) {
            showAccessDeniedMessage();
            return;
        }
        new UserManagement().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnManajemenPenggunaActionPerformed

    private void btnPelaporanPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPelaporanPenjualanActionPerformed
        // Akses: Diizinkan untuk Kasir, Manager, Administrator. Ditolak untuk Staff Gudang.
        if (peran.equals("Staff Gudang")) {
            showAccessDeniedMessage();
            return;
        }
        new LaporanPenjualanForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPelaporanPenjualanActionPerformed

    private void btnPelaporanInventarisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPelaporanInventarisActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new LaporanInventarisForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPelaporanInventarisActionPerformed

    private void btnPelaporanKeuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPelaporanKeuanganActionPerformed
        // Akses: Diizinkan untuk Kasir, Manager, Administrator. Ditolak untuk Staff Gudang.
        if (peran.equals("Staff Gudang")) {
            showAccessDeniedMessage();
            return;
        }
        new LaporanKeuanganForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPelaporanKeuanganActionPerformed

    private void btnLogout1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogout1ActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new Login().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnLogout1ActionPerformed

    private void btnDashboard1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboard1ActionPerformed
        // TODO add your handling code here:
        new MainMenu().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnDashboard1ActionPerformed
    
    private void btnProfileActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
        this.setVisible(false);
        Profile profile = new Profile();
        profile.setVisible(true);
    } 
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LaporanInventarisForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LaporanInventarisForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LaporanInventarisForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LaporanInventarisForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaporanInventarisForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BG_LaporanInventaris;
    private javax.swing.JButton btnDashboard1;
    private javax.swing.JButton btnInventarisPesanan;
    private javax.swing.JButton btnInventarisProduk;
    private javax.swing.JButton btnInventarisSupplier;
    private javax.swing.JButton btnLogout1;
    private javax.swing.JButton btnManajemenDiskon;
    private javax.swing.JButton btnManajemenPengguna;
    private javax.swing.JButton btnManajemenProduk1;
    private javax.swing.JButton btnPOS;
    private javax.swing.JButton btnPelaporanInventaris;
    private javax.swing.JButton btnPelaporanKeuangan;
    private javax.swing.JButton btnPelaporanPenjualan;
    private javax.swing.JButton btnProfile;
    private javax.swing.JButton btnTampilkan;
    private com.toedter.calendar.JDateChooser dateChooserMulai;
    private com.toedter.calendar.JDateChooser dateChooserSelesai;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPeran;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTable tabelLaporan;
    // End of variables declaration//GEN-END:variables
}
