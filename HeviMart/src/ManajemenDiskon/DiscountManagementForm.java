/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ManajemenDiskon;

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
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import util.koneksi;

/**
 *
 * @author Lenovo
 */
public class DiscountManagementForm extends javax.swing.JFrame {

    private DefaultTableModel modelDiscounts;
    private DefaultTableModel modelProducts;

    private String namaLengkap;
//    private String peran;
    private int loggedInUserId;

    /**
     * Creates new form DiscountManagementForm
     */
    public DiscountManagementForm() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Manajemen Diskon & Promosi");

        // Ambil info dari sesi untuk sidebar
        UserSession session = UserSession.getInstance();
        lblUsername.setText(session.getNamaLengkap());
        lblPeran.setText(session.getPeran());

        // Setup tabel diskon
        modelDiscounts = (DefaultTableModel) tblDiscounts.getModel();
        modelDiscounts.setColumnIdentifiers(new Object[]{"ID", "Nama Diskon", "Tipe", "Nilai"});

        // Setup tabel produk dengan kolom tambahan "Status Diskon"
        modelProducts = (DefaultTableModel) tblProducts.getModel();
        modelProducts.setColumnIdentifiers(new Object[]{"ID", "Nama Produk", "Status Diskon"});
        // Atur lebar kolom agar terlihat bagus
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(120);

        loadDiscounts();
        loadProducts(); // Muat semua produk pada awalnya

        // Listener untuk tabel diskon. Ini akan memicu refresh tabel produk
        // setiap kali pengguna memilih diskon yang berbeda.
        tblDiscounts.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            if (!event.getValueIsAdjusting()) {
                // Panggil kembali loadProducts setiap kali seleksi berubah
                loadProducts();
            }
        });
    }

    private void loadDiscounts() {
        modelDiscounts.setRowCount(0);
        try (Connection conn = koneksi.getKoneksi(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT id_diskon, nama_diskon, tipe_diskon, nilai FROM DISKON ORDER BY nama_diskon")) {
            while (rs.next()) {
                modelDiscounts.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBigDecimal(4)});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat diskon: " + e.getMessage());
        }
    }

    private void loadProducts() {
        modelProducts.setRowCount(0);

        // Dapatkan ID diskon yang sedang dipilih. Jika tidak ada, anggap -1.
        int selectedDiscountRow = tblDiscounts.getSelectedRow();
        int selectedDiscountId = -1; // Default jika tidak ada diskon yang dipilih
        if (selectedDiscountRow != -1) {
            selectedDiscountId = (int) tblDiscounts.getValueAt(selectedDiscountRow, 0);
        }

        // Query SQL canggih menggunakan LEFT JOIN dan CASE untuk menentukan status
        String sql = "SELECT p.id_produk, p.nama_produk, "
                + "CASE WHEN dp.id_diskon IS NOT NULL THEN 'Terapkan' ELSE 'Tidak Terapkan' END AS status_diskon "
                + "FROM PRODUK p "
                + "LEFT JOIN DISKON_PRODUK dp ON p.id_produk = dp.id_produk AND dp.id_diskon = ? "
                + "ORDER BY p.nama_produk";

        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selectedDiscountId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                modelProducts.addRow(new Object[]{
                    rs.getInt("id_produk"),
                    rs.getString("nama_produk"),
                    rs.getString("status_diskon")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat produk: " + e.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDiscounts = new javax.swing.JTable();
        btnTambahDiskon = new javax.swing.JButton();
        btnEditDiskon = new javax.swing.JButton();
        btnHapusDiskon = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();
        btnApplyDiscount = new javax.swing.JButton();
        btnRemoveDiscount = new javax.swing.JButton();
        lblPeran = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
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
        btnProfileDiskon = new javax.swing.JButton();
        BG_DiskonManagement = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblDiscounts.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblDiscounts);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 550, 620, -1));

        btnTambahDiskon.setBorderPainted(false);
        btnTambahDiskon.setContentAreaFilled(false);
        btnTambahDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahDiskonActionPerformed(evt);
            }
        });
        jPanel1.add(btnTambahDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 172, 240, 50));

        btnEditDiskon.setBorderPainted(false);
        btnEditDiskon.setContentAreaFilled(false);
        btnEditDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditDiskonActionPerformed(evt);
            }
        });
        jPanel1.add(btnEditDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(942, 293, 240, 50));

        btnHapusDiskon.setBorderPainted(false);
        btnHapusDiskon.setContentAreaFilled(false);
        btnHapusDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusDiskonActionPerformed(evt);
            }
        });
        jPanel1.add(btnHapusDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 412, 240, 50));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblProducts);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 40, 620, -1));

        btnApplyDiscount.setBorderPainted(false);
        btnApplyDiscount.setContentAreaFilled(false);
        btnApplyDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyDiscountActionPerformed(evt);
            }
        });
        jPanel1.add(btnApplyDiscount, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 650, 340, 60));

        btnRemoveDiscount.setBorderPainted(false);
        btnRemoveDiscount.setContentAreaFilled(false);
        btnRemoveDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveDiscountActionPerformed(evt);
            }
        });
        jPanel1.add(btnRemoveDiscount, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 750, 350, 60));

        lblPeran.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblPeran.setForeground(new java.awt.Color(30, 41, 59));
        lblPeran.setText("peran");
        jPanel1.add(lblPeran, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 120, 20));

        lblUsername.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(30, 41, 59));
        lblUsername.setText("username");
        jPanel1.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, 120, 20));

        btnDashboard1.setBorderPainted(false);
        btnDashboard1.setContentAreaFilled(false);
        btnDashboard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboard1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnDashboard1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 160, 30));

        btnPOS.setBorderPainted(false);
        btnPOS.setContentAreaFilled(false);
        btnPOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPOSActionPerformed(evt);
            }
        });
        jPanel1.add(btnPOS, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 258, 160, 30));

        btnManajemenDiskon.setBorderPainted(false);
        btnManajemenDiskon.setContentAreaFilled(false);
        btnManajemenDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManajemenDiskonActionPerformed(evt);
            }
        });
        jPanel1.add(btnManajemenDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 307, 160, 50));

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

        btnInventarisSupplier.setBorderPainted(false);
        btnInventarisSupplier.setContentAreaFilled(false);
        btnInventarisSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisSupplierActionPerformed(evt);
            }
        });
        jPanel1.add(btnInventarisSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 502, 110, 20));

        btnInventarisProduk.setBorderPainted(false);
        btnInventarisProduk.setContentAreaFilled(false);
        btnInventarisProduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisProdukActionPerformed(evt);
            }
        });
        jPanel1.add(btnInventarisProduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 530, 110, 30));

        btnInventarisPesanan.setBorderPainted(false);
        btnInventarisPesanan.setContentAreaFilled(false);
        btnInventarisPesanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisPesananActionPerformed(evt);
            }
        });
        jPanel1.add(btnInventarisPesanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 570, 110, 30));

        btnManajemenPengguna.setBorderPainted(false);
        btnManajemenPengguna.setContentAreaFilled(false);
        btnManajemenPengguna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManajemenPenggunaActionPerformed(evt);
            }
        });
        jPanel1.add(btnManajemenPengguna, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 610, 160, 60));

        btnPelaporanPenjualan.setBorderPainted(false);
        btnPelaporanPenjualan.setContentAreaFilled(false);
        btnPelaporanPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanPenjualanActionPerformed(evt);
            }
        });
        jPanel1.add(btnPelaporanPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 720, 110, 20));

        btnPelaporanInventaris.setBorderPainted(false);
        btnPelaporanInventaris.setContentAreaFilled(false);
        btnPelaporanInventaris.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanInventarisActionPerformed(evt);
            }
        });
        jPanel1.add(btnPelaporanInventaris, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 745, 110, 20));

        btnPelaporanKeuangan.setBorderPainted(false);
        btnPelaporanKeuangan.setContentAreaFilled(false);
        btnPelaporanKeuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanKeuanganActionPerformed(evt);
            }
        });
        jPanel1.add(btnPelaporanKeuangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 770, 70, 20));

        btnLogout1.setBorderPainted(false);
        btnLogout1.setContentAreaFilled(false);
        btnLogout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogout1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnLogout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 930, 130, 40));

        btnProfileDiskon.setBorderPainted(false);
        btnProfileDiskon.setContentAreaFilled(false);
        btnProfileDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileDiskonActionPerformed(evt);
            }
        });
        jPanel1.add(btnProfileDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 180, 50));

        BG_DiskonManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Manajemen Diskon.png"))); // NOI18N
        jPanel1.add(BG_DiskonManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

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
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnApplyDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyDiscountActionPerformed
        // TODO add your handling code here:
        int selectedDiscountRow = tblDiscounts.getSelectedRow();
        int[] selectedProductRows = tblProducts.getSelectedRows();

        if (selectedDiscountRow == -1 || selectedProductRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Pilih satu diskon dan minimal satu produk.");
            return;
        }

        int idDiskon = (int) tblDiscounts.getValueAt(selectedDiscountRow, 0);

        // INSERT IGNORE digunakan agar tidak terjadi error jika relasi sudah ada (duplicate entry)
//        String sql = "INSERT IGNORE INTO DISKON_PRODUK (id_diskon, id_produk) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = koneksi.getKoneksi();
            conn.setAutoCommit(false);
            String checkSql = "SELECT d.nama_diskon FROM DISKON_PRODUK dp JOIN DISKON d ON dp.id_diskon = d.id_diskon WHERE dp.id_produk = ?";

            // 3. Query untuk menghapus diskon lama (jika ada) dan menerapkan yang baru
            // Ini adalah cara paling efisien untuk mengganti diskon.
            // ON DUPLICATE KEY UPDATE akan melakukan UPDATE jika produk sudah ada, dan INSERT jika belum.
            // PENTING: Ini memerlukan PRIMARY KEY atau UNIQUE index pada `id_produk` di tabel `diskon_produk`.
            String upsertSql = "INSERT INTO DISKON_PRODUK (id_produk, id_diskon) VALUES (?, ?) ON DUPLICATE KEY UPDATE id_diskon = VALUES(id_diskon)";

            // Sebelum memulai, kita buat tabel diskon_produk memiliki UNIQUE key pada id_produk
            try (Statement stmt = conn.createStatement()) {
                // Coba hapus constraint lama jika ada, abaikan error jika tidak ada
                try {
                    stmt.execute("ALTER TABLE DISKON_PRODUK DROP INDEX id_produk;");
                } catch (SQLException e) {
                    /* abaikan */ }
                // Tambahkan constraint UNIQUE baru
                stmt.execute("ALTER TABLE DISKON_PRODUK ADD UNIQUE (id_produk);");
            }

            try (PreparedStatement pstmtUpsert = conn.prepareStatement(upsertSql)) {
                for (int row : selectedProductRows) {
                    int idProduk = (int) tblProducts.getValueAt(row, 0);

                    pstmtUpsert.setInt(1, idProduk);
                    pstmtUpsert.setInt(2, idDiskon);
                    pstmtUpsert.addBatch();
                }
                pstmtUpsert.executeBatch();
            }

            conn.commit(); // Konfirmasi semua perubahan
            JOptionPane.showMessageDialog(this, "Diskon berhasil diterapkan ke produk terpilih!");

            // Refresh tabel untuk melihat status terbaru
            loadProducts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menerapkan diskon: " + e.getMessage());
        }
    }//GEN-LAST:event_btnApplyDiscountActionPerformed

    private void btnTambahDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahDiskonActionPerformed
        // TODO add your handling code here:
        new AddEditDiscountDialog(this, true, 0).setVisible(true);
        loadDiscounts();
    }//GEN-LAST:event_btnTambahDiskonActionPerformed

    private void btnEditDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditDiskonActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblDiscounts.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih diskon yang akan diedit.");
            return;
        }
        int discountId = (int) tblDiscounts.getValueAt(selectedRow, 0);
        new AddEditDiscountDialog(this, true, discountId).setVisible(true);
        loadDiscounts();
    }//GEN-LAST:event_btnEditDiskonActionPerformed

    private void btnRemoveDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveDiscountActionPerformed
        // TODO add your handling code here:
        // 1. Dapatkan baris yang dipilih dari kedua tabel
        int selectedDiscountRow = tblDiscounts.getSelectedRow();
        int[] selectedProductRows = tblProducts.getSelectedRows();

        // 2. Validasi apakah pengguna sudah memilih diskon dan produk
        if (selectedDiscountRow == -1 || selectedProductRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Harap pilih satu diskon dan minimal satu produk yang diskonnya akan dihapus.",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Dapatkan ID diskon yang dipilih untuk digunakan dalam query dan pesan konfirmasi
        int idDiskon = (int) tblDiscounts.getValueAt(selectedDiscountRow, 0);
        String namaDiskon = (String) tblDiscounts.getValueAt(selectedDiscountRow, 1);

        // 4. Minta konfirmasi dari pengguna sebelum menghapus
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus diskon '" + namaDiskon + "' dari produk yang dipilih?",
                "Konfirmasi Hapus Relasi Diskon",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return; // Batalkan proses jika pengguna memilih 'Tidak'
        }

        // 5. Siapkan query SQL untuk menghapus data dari tabel relasi DISKON_PRODUK
        String sql = "DELETE FROM DISKON_PRODUK WHERE id_diskon = ? AND id_produk = ?";

        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // Mulai transaksi

            // 6. Lakukan iterasi untuk setiap produk yang dipilih
            for (int row : selectedProductRows) {
                int idProduk = (int) tblProducts.getValueAt(row, 0);

                // Set parameter untuk query DELETE
                pstmt.setInt(1, idDiskon);
                pstmt.setInt(2, idProduk);

                // Tambahkan perintah ke dalam batch
                pstmt.addBatch();
            }

            // 7. Eksekusi semua perintah dalam batch
            pstmt.executeBatch();
            conn.commit(); // Selesaikan transaksi jika semua berhasil

            JOptionPane.showMessageDialog(this,
                    "Diskon berhasil dihapus dari produk terpilih.",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            // Tampilkan pesan error jika terjadi kegagalan
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus diskon dari produk: " + e.getMessage(),
                    "Error Database",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRemoveDiscountActionPerformed

    private void showAccessDeniedMessage() {
        JOptionPane.showMessageDialog(this,
                "Anda tidak memiliki hak akses untuk membuka menu ini.",
                "Akses Ditolak",
                JOptionPane.WARNING_MESSAGE);
    }

    private void btnPOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPOSActionPerformed
        // Akses: Diizinkan untuk Kasir, Manager, Administrator. Ditolak untuk Staff Gudang.
        String peran = UserSession.getInstance().getPeran();
        if (peran.equals("Staff Gudang")) {
            showAccessDeniedMessage();
            return;
        }
        new POSForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPOSActionPerformed

    private void btnManajemenDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManajemenDiskonActionPerformed
        // Akses: Diizinkan untuk Kasir, Manager, Administrator. Ditolak untuk Staff Gudang.
        JOptionPane.showMessageDialog(this, "Anda sudah berada di halaman Manajemen Diskon.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnManajemenDiskonActionPerformed

    private void btnManajemenProduk1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnManajemenProduk1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnManajemenProduk1MouseClicked

    private void btnManajemenProduk1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManajemenProduk1ActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        String peran = UserSession.getInstance().getPeran();
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new ProdukForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnManajemenProduk1ActionPerformed

    private void btnInventarisSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisSupplierActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        String peran = UserSession.getInstance().getPeran();
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new SupplierForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnInventarisSupplierActionPerformed

    private void btnInventarisProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisProdukActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        String peran = UserSession.getInstance().getPeran();
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new PurchaseOrderForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnInventarisProdukActionPerformed

    private void btnInventarisPesananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisPesananActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        String peran = UserSession.getInstance().getPeran();
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new GoodsReceiptForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnInventarisPesananActionPerformed

    private void btnManajemenPenggunaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManajemenPenggunaActionPerformed
        // Akses: Hanya untuk Manager dan Administrator.
        String peran = UserSession.getInstance().getPeran();
        if (!peran.equals("Administrator") && !peran.equals("Manager")) {
            showAccessDeniedMessage();
            return;
        }
        new UserManagement().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnManajemenPenggunaActionPerformed

    private void btnPelaporanPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPelaporanPenjualanActionPerformed
        // Akses: Diizinkan untuk Kasir, Manager, Administrator. Ditolak untuk Staff Gudang.
        String peran = UserSession.getInstance().getPeran();
        if (peran.equals("Staff Gudang")) {
            showAccessDeniedMessage();
            return;
        }
        new LaporanPenjualanForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPelaporanPenjualanActionPerformed

    private void btnPelaporanInventarisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPelaporanInventarisActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        String peran = UserSession.getInstance().getPeran();
        if (peran.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new LaporanInventarisForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPelaporanInventarisActionPerformed

    private void btnPelaporanKeuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPelaporanKeuanganActionPerformed
        // Akses: Diizinkan untuk Kasir, Manager, Administrator. Ditolak untuk Staff Gudang.
        String peran = UserSession.getInstance().getPeran();
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

    private void btnHapusDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusDiskonActionPerformed
        // TODO add your handling code here:
        // 1. Dapatkan baris yang dipilih dari tabel diskon (tabel bawah)
        int selectedRow = tblDiscounts.getSelectedRow();

        // 2. Validasi: Pastikan ada diskon yang dipilih
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih diskon yang akan dihapus dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Konfirmasi Pengguna: Ini adalah langkah keamanan yang penting
        String namaDiskon = (String) tblDiscounts.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus diskon '" + namaDiskon + "'?\nSemua produk yang terhubung dengan diskon ini akan kehilangan promosinya.",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        // 4. Jika pengguna tidak menekan 'YES', batalkan proses
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 5. Eksekusi Hapus ke Database
        int idDiskon = (int) tblDiscounts.getValueAt(selectedRow, 0);
        String sql = "DELETE FROM DISKON WHERE id_diskon = ?";

        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDiskon);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Diskon berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                // 6. Refresh tabel untuk menampilkan perubahan
                loadDiscounts();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus diskon. Data mungkin sudah tidak ada.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus diskon: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnHapusDiskonActionPerformed

    private void btnProfileDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileDiskonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        Profile profile = new Profile(this);
        profile.setVisible(true);
    }//GEN-LAST:event_btnProfileDiskonActionPerformed

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
            java.util.logging.Logger.getLogger(DiscountManagementForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DiscountManagementForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DiscountManagementForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DiscountManagementForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DiscountManagementForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BG_DiskonManagement;
    private javax.swing.JButton btnApplyDiscount;
    private javax.swing.JButton btnDashboard1;
    private javax.swing.JButton btnEditDiskon;
    private javax.swing.JButton btnHapusDiskon;
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
    private javax.swing.JButton btnProfileDiskon;
    private javax.swing.JButton btnRemoveDiscount;
    private javax.swing.JButton btnTambahDiskon;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblPeran;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTable tblDiscounts;
    private javax.swing.JTable tblProducts;
    // End of variables declaration//GEN-END:variables
}
