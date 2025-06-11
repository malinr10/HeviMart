/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package MainForm;
import Login.Login;
import ManajemenProduk.ProdukForm;
import PosSistem.POSForm;
import util.koneksi;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Lenovo
 */
public class MainMenu extends javax.swing.JFrame {

    private String namaLengkap;
    private String peran;
    private DefaultTableModel modelTabelProduk;
    private int loggedInUserId;

    /**
     * Creates new form MainMenu
     */
    public MainMenu(int idPengguna, String namaLengkap, String peran) {
        initComponents();
        
        this.loggedInUserId = idPengguna;
        this.namaLengkap = namaLengkap;
        this.peran = peran;
        
        // Setup awal
        this.setLocationRelativeTo(null); // Form di tengah
        
        // Atur tombol berdasarkan peran pengguna
        aturTombolBerdasarkanPeran();
        
        // Setup model untuk tabel dashboard
        modelTabelProduk = new DefaultTableModel();
        tblPenjualanProduk.setModel(modelTabelProduk);
        modelTabelProduk.addColumn("Nama Barang");
        modelTabelProduk.addColumn("Jumlah Terjual");
        modelTabelProduk.addColumn("Total Pemasukan");
        
        // Muat semua data untuk dashboard
        loadDashboardData();
    }
    
    private void aturTombolBerdasarkanPeran() {
        if (this.peran.equals("Kasir")) {
//            jButton1.setEnabled(false);
            btnManajemenPengguna.setEnabled(false);
            btnPelaporanPenjualan.setEnabled(false);
            btnPelaporanInventaris.setEnabled(false);
            btnPelaporanKeuangan.setEnabled(false);
        } else if (this.peran.equals("Staff Gudang")) {
            btnPOS.setEnabled(false);
            btnManajemenPengguna.setEnabled(false);
            btnPelaporanPenjualan.setEnabled(false);
            btnPelaporanInventaris.setEnabled(false);
            btnPelaporanKeuangan.setEnabled(false);
        }
        // Administrator dan Manager bisa mengakses semua
    }
    
    private void loadDashboardData() {
        // Menggunakan Locale Indonesia untuk format mata uang Rupiah (Rp)
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        try {
            Connection conn = koneksi.getKoneksi();
            Statement stmt = conn.createStatement();
            
            // 1. Ambil Total Penjualan (Bulan Ini)
            String sqlPenjualan = "SELECT SUM(harga_akhir) AS total FROM TRANSAKSI WHERE MONTH(tanggal_transaksi) = MONTH(CURRENT_DATE()) AND YEAR(tanggal_transaksi) = YEAR(CURRENT_DATE())";
            ResultSet rsPenjualan = stmt.executeQuery(sqlPenjualan);
            if (rsPenjualan.next()) {
                double total = rsPenjualan.getDouble("total");
                lblTotalPenjualan.setText(formatRupiah.format(total));
            }

            // 2. Ambil Total Transaksi (Bulan Ini)
            String sqlTransaksi = "SELECT COUNT(id_transaksi) AS jumlah FROM TRANSAKSI WHERE MONTH(tanggal_transaksi) = MONTH(CURRENT_DATE()) AND YEAR(tanggal_transaksi) = YEAR(CURRENT_DATE())";
            ResultSet rsTransaksi = stmt.executeQuery(sqlTransaksi);
            if (rsTransaksi.next()) {
                lblTotalTransaksi.setText(rsTransaksi.getString("jumlah") + " Transaksi");
            }

            // 3. Ambil Total Pemasukan (Bulan Ini)
            String sqlTotalPemasukan = "SELECT SUM(harga_akhir) AS total_pemasukan FROM TRANSAKSI WHERE MONTH(tanggal_transaksi) = MONTH(CURRENT_DATE()) AND YEAR(tanggal_transaksi) = YEAR(CURRENT_DATE())";
            ResultSet rsTotalPemasukan = stmt.executeQuery(sqlTotalPemasukan);
            if (rsTotalPemasukan.next()) {
                double totalPemasukan = rsTotalPemasukan.getDouble("total_pemasukan");
                lblTotalPemasukan.setText(formatRupiah.format(totalPemasukan));
            } else {
                lblTotalPemasukan.setText(formatRupiah.format(0.0));
            }

            // 4. Hitung Total Profit (Bulan Ini)
            // Asumsi: total_modal dihitung dari jumlah * harga_beli produk dalam DETAIL_TRANSAKSI
            String sqlTotalProfit = "SELECT " +
                                    "COALESCE(SUM(t.harga_akhir), 0) AS total_penjualan, " +
                                    "COALESCE(SUM(dt.jumlah * p.harga_beli), 0) AS total_modal " +
                                    "FROM TRANSAKSI t " +
                                    "JOIN DETAIL_TRANSAKSI dt ON t.id_transaksi = dt.id_transaksi " +
                                    "JOIN PRODUK p ON dt.id_produk = p.id_produk " +
                                    "WHERE MONTH(t.tanggal_transaksi) = MONTH(CURRENT_DATE()) AND YEAR(t.tanggal_transaksi) = YEAR(CURRENT_DATE())";

            ResultSet rsTotalProfit = stmt.executeQuery(sqlTotalProfit);
            if (rsTotalProfit.next()) {
                double totalPenjualan = rsTotalProfit.getDouble("total_penjualan");
                double totalModal = rsTotalProfit.getDouble("total_modal");
                double profit = totalPenjualan - totalModal;
                lblTotalProfit.setText(formatRupiah.format(profit));
            } else {
                lblTotalProfit.setText(formatRupiah.format(0.0));
            }
            
            // 5. Ambil 10 Produk Terlaris untuk Tabel
            modelTabelProduk.setRowCount(0); // Kosongkan tabel
            String sqlProdukTerlaris = "SELECT p.nama_produk, SUM(dt.jumlah) AS jumlah_terjual, SUM(dt.subtotal) AS total_pemasukan " +
                                       "FROM DETAIL_TRANSAKSI dt JOIN PRODUK p ON dt.id_produk = p.id_produk " +
                                       "GROUP BY p.nama_produk ORDER BY jumlah_terjual DESC LIMIT 10";
            ResultSet rsProduk = stmt.executeQuery(sqlProdukTerlaris);
            while(rsProduk.next()) {
                modelTabelProduk.addRow(new Object[]{
                    rsProduk.getString("nama_produk"),
                    rsProduk.getInt("jumlah_terjual"),
                    formatRupiah.format(rsProduk.getDouble("total_pemasukan"))
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data dashboard: " + e.getMessage());
            e.printStackTrace();
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

        jPanel8 = new javax.swing.JPanel();
        lblTotalPemasukan = new javax.swing.JLabel();
        lblTotalTransaksi = new javax.swing.JLabel();
        lblTotalPenjualan = new javax.swing.JLabel();
        lblTotalProfit = new javax.swing.JLabel();
        txtPencarian = new javax.swing.JTextField();
        btnPencarian = new javax.swing.JButton();
        lblPeran = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPenjualanProduk = new javax.swing.JTable();
        btnDashboard1 = new javax.swing.JButton();
        btnPOS = new javax.swing.JButton();
        btnManajemenDiskon = new javax.swing.JButton();
        btnManajemenProduk1 = new javax.swing.JButton();
        btnInventarisSupplier = new javax.swing.JButton();
        btnInventarisProduk = new javax.swing.JButton();
        btnInventarisPesanan = new javax.swing.JButton();
        btnManajemenPengguna = new javax.swing.JButton();
        btnPelaporanPenjualan = new javax.swing.JButton();
        btnPelaporanInventaris = new javax.swing.JButton();
        btnPelaporanKeuangan = new javax.swing.JButton();
        btnLogout1 = new javax.swing.JButton();
        BG_Dashboard = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel8.setMinimumSize(new java.awt.Dimension(1440, 1024));
        jPanel8.setPreferredSize(new java.awt.Dimension(1440, 1024));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTotalPemasukan.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 36)); // NOI18N
        lblTotalPemasukan.setText("Pemasukan");
        jPanel8.add(lblTotalPemasukan, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 250, -1, -1));

        lblTotalTransaksi.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 36)); // NOI18N
        lblTotalTransaksi.setText("Total Transaksi");
        jPanel8.add(lblTotalTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 420, -1, -1));

        lblTotalPenjualan.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 36)); // NOI18N
        lblTotalPenjualan.setText("Total Penjualan");
        jPanel8.add(lblTotalPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 250, -1, -1));

        lblTotalProfit.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 36)); // NOI18N
        lblTotalProfit.setText("Keuntungan");
        jPanel8.add(lblTotalProfit, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 420, -1, -1));

        txtPencarian.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtPencarian.setBorder(null);
        jPanel8.add(txtPencarian, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 580, 270, 30));

        btnPencarian.setBorderPainted(false);
        btnPencarian.setContentAreaFilled(false);
        jPanel8.add(btnPencarian, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 580, 30, 30));

        lblPeran.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblPeran.setForeground(new java.awt.Color(30, 41, 59));
        lblPeran.setText("peran");
        jPanel8.add(lblPeran, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 120, 20));

        lblUsername.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(30, 41, 59));
        lblUsername.setText("username");
        jPanel8.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, 120, 20));

        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        tblPenjualanProduk.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tblPenjualanProduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "No", "Nama Produk", "Jumlah Terjual", "Total Pemasukan"
            }
        ));
        jScrollPane2.setViewportView(tblPenjualanProduk);

        jPanel8.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(322, 637, 980, 290));

        btnDashboard1.setBorderPainted(false);
        btnDashboard1.setContentAreaFilled(false);
        jPanel8.add(btnDashboard1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 160, 30));

        btnPOS.setBorderPainted(false);
        btnPOS.setContentAreaFilled(false);
        jPanel8.add(btnPOS, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 258, 80, 30));

        btnManajemenDiskon.setBorderPainted(false);
        btnManajemenDiskon.setContentAreaFilled(false);
        btnManajemenDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManajemenDiskonActionPerformed(evt);
            }
        });
        jPanel8.add(btnManajemenDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 307, 160, 50));

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
        jPanel8.add(btnManajemenProduk1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, 160, 50));

        btnInventarisSupplier.setBorderPainted(false);
        btnInventarisSupplier.setContentAreaFilled(false);
        btnInventarisSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisSupplierActionPerformed(evt);
            }
        });
        jPanel8.add(btnInventarisSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 502, 60, 20));

        btnInventarisProduk.setBorderPainted(false);
        btnInventarisProduk.setContentAreaFilled(false);
        btnInventarisProduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisProdukActionPerformed(evt);
            }
        });
        jPanel8.add(btnInventarisProduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 530, 80, 30));

        btnInventarisPesanan.setBorderPainted(false);
        btnInventarisPesanan.setContentAreaFilled(false);
        btnInventarisPesanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisPesananActionPerformed(evt);
            }
        });
        jPanel8.add(btnInventarisPesanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 570, 80, 30));

        btnManajemenPengguna.setBorderPainted(false);
        btnManajemenPengguna.setContentAreaFilled(false);
        jPanel8.add(btnManajemenPengguna, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 610, 160, 60));

        btnPelaporanPenjualan.setBorderPainted(false);
        btnPelaporanPenjualan.setContentAreaFilled(false);
        btnPelaporanPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanPenjualanActionPerformed(evt);
            }
        });
        jPanel8.add(btnPelaporanPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 720, 70, 20));

        btnPelaporanInventaris.setBorderPainted(false);
        btnPelaporanInventaris.setContentAreaFilled(false);
        btnPelaporanInventaris.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanInventarisActionPerformed(evt);
            }
        });
        jPanel8.add(btnPelaporanInventaris, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 745, 70, 20));

        btnPelaporanKeuangan.setBorderPainted(false);
        btnPelaporanKeuangan.setContentAreaFilled(false);
        btnPelaporanKeuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanKeuanganActionPerformed(evt);
            }
        });
        jPanel8.add(btnPelaporanKeuangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 770, 70, 20));

        btnLogout1.setBorderPainted(false);
        btnLogout1.setContentAreaFilled(false);
        btnLogout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogout1ActionPerformed(evt);
            }
        });
        jPanel8.add(btnLogout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 930, 130, 40));

        BG_Dashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Dashboard.png"))); // NOI18N
        jPanel8.add(BG_Dashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(jPanel8, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogout1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogout1ActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new Login().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnLogout1ActionPerformed

    private void btnPelaporanKeuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPelaporanKeuanganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPelaporanKeuanganActionPerformed

    private void btnPelaporanInventarisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPelaporanInventarisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPelaporanInventarisActionPerformed

    private void btnPelaporanPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPelaporanPenjualanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPelaporanPenjualanActionPerformed

    private void btnInventarisPesananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisPesananActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInventarisPesananActionPerformed

    private void btnInventarisProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisProdukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInventarisProdukActionPerformed

    private void btnInventarisSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisSupplierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInventarisSupplierActionPerformed

    private void btnManajemenProduk1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManajemenProduk1ActionPerformed
        // TODO add your handling code here:
        new ProdukForm().setVisible(true);
    }//GEN-LAST:event_btnManajemenProduk1ActionPerformed

    private void btnManajemenProduk1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnManajemenProduk1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnManajemenProduk1MouseClicked

    private void btnManajemenDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManajemenDiskonActionPerformed
        // TODO add your handling code here:
        new POSForm(this.loggedInUserId).setVisible(true);
    }//GEN-LAST:event_btnManajemenDiskonActionPerformed

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
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BG_Dashboard;
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
    private javax.swing.JButton btnPencarian;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblPeran;
    private javax.swing.JLabel lblTotalPemasukan;
    private javax.swing.JLabel lblTotalPenjualan;
    private javax.swing.JLabel lblTotalProfit;
    private javax.swing.JLabel lblTotalTransaksi;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTable tblPenjualanProduk;
    private javax.swing.JTextField txtPencarian;
    // End of variables declaration//GEN-END:variables
}
