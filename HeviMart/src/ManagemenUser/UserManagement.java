/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ManagemenUser;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import PosSistem.POSForm;
import util.koneksi;
import util.UserSession;
import Profile.Profile;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author mdr
 */
public class UserManagement extends javax.swing.JFrame {

    private DefaultTableModel modelTabelPengguna;
    private UserDAO userDAO;
    private int currentLoggedInUserId; // Untuk menyimpan ID user yang sedang login
    private String currentLoggedInUserRole; // Untuk menyimpan peran user yang sedang login

    private String namaLengkap;
    private String peran;
    private int loggedInUserId;

    public UserManagement() { // Constructor menerima data user login
        initComponents();
        this.setLocationRelativeTo(null); // Posisikan di tengah layar

        UserSession session = UserSession.getInstance();
        String namaLengkap = session.getNamaLengkap();
        this.loggedInUserId = session.getIdPengguna();
        this.currentLoggedInUserId = session.getIdPengguna();
        this.currentLoggedInUserRole = session.getPeran();
        lblUsername.setText(this.namaLengkap);
        lblPeran.setText(this.peran);

        modelTabelPengguna = new DefaultTableModel();
        tblPengguna.setModel(modelTabelPengguna);
        modelTabelPengguna.addColumn("ID");
        modelTabelPengguna.addColumn("Nama Lengkap");
        modelTabelPengguna.addColumn("Username"); // Sesuaikan dengan kolom di UserDAO (nama_pengguna)
        modelTabelPengguna.addColumn("Peran");
        modelTabelPengguna.addColumn("Email");
        modelTabelPengguna.addColumn("Telepon");
        modelTabelPengguna.addColumn("Aktif"); // Tambahkan kolom Aktif

        userDAO = new UserDAO(); // Inisialisasi DAO

        loadUserData(); // Muat data saat panel pertama kali dibuka

        // Tambahkan listener untuk live search
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchAndLoadUserData();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchAndLoadUserData();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchAndLoadUserData();
            }
        });

        // Tambahkan event listeners untuk tombol-tombol
        addEventListeners();
    }

    private void addEventListeners() {
        btnTambahUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserFormDialog(null); // null menandakan mode tambah
            }
        });

        btnEditUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblPengguna.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) modelTabelPengguna.getValueAt(selectedRow, 0); // Ambil ID dari kolom pertama
                    openUserFormDialog(userId); // Buka dialog edit dengan ID pengguna
                } else {
                    JOptionPane.showMessageDialog(UserManagement.this, "Pilih pengguna yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnGantiPswd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblPengguna.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) modelTabelPengguna.getValueAt(selectedRow, 0);
                    // Pastikan tidak ada pengecekan peran di sini jika ini tombol untuk ganti password user lain oleh admin
                    openChangePasswordDialog(userId); // Buka dialog ganti password
                } else {
                    JOptionPane.showMessageDialog(UserManagement.this, "Pilih pengguna untuk mengganti password.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnDeleteUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblPengguna.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) modelTabelPengguna.getValueAt(selectedRow, 0);
                    String usernameToDelete = (String) modelTabelPengguna.getValueAt(selectedRow, 2); // Ambil username dari kolom ke-2

                    // Pencegahan: Admin tidak bisa menghapus akunnya sendiri
                    if (userId == currentLoggedInUserId) {
                        JOptionPane.showMessageDialog(UserManagement.this, "Anda tidak bisa menghapus akun Anda sendiri.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(UserManagement.this, "Apakah Anda yakin ingin menghapus pengguna '" + usernameToDelete + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteUser(userId);
                    }
                } else {
                    JOptionPane.showMessageDialog(UserManagement.this, "Pilih pengguna yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText(""); // Kosongkan search field
                loadUserData(); // Muat ulang semua data
            }
        });
    }

    // Metode untuk memuat data pengguna dari database (tanpa filter/search)
    private void loadUserData() {
        modelTabelPengguna.setRowCount(0); // Kosongkan tabel
        List<User> users = userDAO.getAllUsers(); // Panggil method dari UserDAO
        for (User user : users) {
            modelTabelPengguna.addRow(new Object[]{
                user.getIdPengguna(),
                user.getNamaLengkap(),
                user.getNamaPengguna(),
                user.getPeran(),
                user.getEmail(),
                user.getTelepon(),
                user.isAktif() ? "Ya" : "Tidak" // Tampilkan 'Ya'/'Tidak' untuk status aktif
            });
        }
        // Atur lebar kolom (opsional, bisa diatur juga di desainer NetBeans)
        tblPengguna.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        tblPengguna.getColumnModel().getColumn(1).setPreferredWidth(150); // Nama Lengkap
        tblPengguna.getColumnModel().getColumn(2).setPreferredWidth(100); // Username
        tblPengguna.getColumnModel().getColumn(3).setPreferredWidth(80); // Role
        tblPengguna.getColumnModel().getColumn(4).setPreferredWidth(150); // Email
        tblPengguna.getColumnModel().getColumn(5).setPreferredWidth(100); // Telepon
        tblPengguna.getColumnModel().getColumn(6).setPreferredWidth(50); // Aktif
    }

    // Metode untuk memuat data pengguna dari database dengan filter/search
    private void searchAndLoadUserData() {
        String keyword = txtSearch.getText();
        modelTabelPengguna.setRowCount(0); // Kosongkan tabel
        List<User> users = userDAO.searchUsers(keyword); // Panggil method search dari UserDAO
        for (User user : users) {
            modelTabelPengguna.addRow(new Object[]{
                user.getIdPengguna(),
                user.getNamaLengkap(),
                user.getNamaPengguna(),
                user.getPeran(),
                user.getEmail(),
                user.getTelepon(),
                user.isAktif() ? "Ya" : "Tidak"
            });
        }
    }

    // Metode untuk menampilkan dialog tambah/edit pengguna
    private void openUserFormDialog(Integer userId) {
        UserFormDialog dialog;
        if (userId == null) {
            // Mode tambah pengguna baru
            dialog = new UserFormDialog(this, true);
        } else {
            // Mode edit pengguna yang sudah ada
            dialog = new UserFormDialog(this, true, userId);
        }
        dialog.setVisible(true);
        // Jika data disimpan di dialog (addUser/updateUser berhasil), muat ulang tabel
        if (dialog.isDataSaved()) {
            loadUserData();
        }
    }

    // Metode untuk menampilkan dialog ganti password
    private void openChangePasswordDialog(int userId) {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, true, userId);
        dialog.setVisible(true);
        // Tidak perlu me-reload tabel setelah ganti password, data tabel tidak berubah
    }

    // Metode untuk menghapus pengguna
    private void deleteUser(int userId) {
        if (userDAO.deleteUser(userId)) {
            JOptionPane.showMessageDialog(this, "Pengguna berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadUserData(); // Muat ulang tabel
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus pengguna.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metode untuk mengatur visibilitas tombol berdasarkan peran
//    private void checkUserRolePermissions() {
//        if (!currentLoggedInUserRole.equalsIgnoreCase("Admin")) {
//            btnTambahUser.setEnabled(false);
//            btnEditUser.setEnabled(false);
//            btnGantiPswd.setEnabled(false);
//            btnDeleteUser.setEnabled(false);
//        }
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnGantiPswd = new javax.swing.JButton();
        btnEditUser = new javax.swing.JButton();
        btnDeleteUser = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnTambahUser = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPengguna = new javax.swing.JTable();
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
        btnProfilePurchase = new javax.swing.JButton();
        BG_ManajemenUser = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnGantiPswd.setBorderPainted(false);
        btnGantiPswd.setContentAreaFilled(false);
        jPanel2.add(btnGantiPswd, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 910, 220, 60));

        btnEditUser.setBorderPainted(false);
        btnEditUser.setContentAreaFilled(false);
        btnEditUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditUserActionPerformed(evt);
            }
        });
        jPanel2.add(btnEditUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 910, 220, 60));

        btnDeleteUser.setBorderPainted(false);
        btnDeleteUser.setContentAreaFilled(false);
        btnDeleteUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteUserActionPerformed(evt);
            }
        });
        jPanel2.add(btnDeleteUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 910, 230, 60));

        txtSearch.setBorder(null);
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        jPanel2.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 287, 680, 40));
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 300, -1, -1));

        btnTambahUser.setBorderPainted(false);
        btnTambahUser.setContentAreaFilled(false);
        btnTambahUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahUserActionPerformed(evt);
            }
        });
        jPanel2.add(btnTambahUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 280, 220, 60));
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 400, -1, -1));

        btnRefresh.setBorderPainted(false);
        btnRefresh.setContentAreaFilled(false);
        jPanel2.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 380, 70, 50));

        tblPengguna.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPengguna.setName(""); // NOI18N
        jScrollPane1.setViewportView(tblPengguna);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 460, 1100, -1));

        lblPeran.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblPeran.setForeground(new java.awt.Color(30, 41, 59));
        lblPeran.setText("peran");
        jPanel2.add(lblPeran, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 120, 20));

        lblUsername.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(30, 41, 59));
        lblUsername.setText("username");
        jPanel2.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, 120, 20));

        btnDashboard1.setBorderPainted(false);
        btnDashboard1.setContentAreaFilled(false);
        btnDashboard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboard1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnDashboard1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 160, 30));

        btnPOS.setBorderPainted(false);
        btnPOS.setContentAreaFilled(false);
        btnPOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPOSActionPerformed(evt);
            }
        });
        jPanel2.add(btnPOS, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 258, 160, 30));

        btnManajemenDiskon.setBorderPainted(false);
        btnManajemenDiskon.setContentAreaFilled(false);
        btnManajemenDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManajemenDiskonActionPerformed(evt);
            }
        });
        jPanel2.add(btnManajemenDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 307, 160, 50));

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
        jPanel2.add(btnManajemenProduk1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, 160, 50));

        btnInventarisSupplier.setBorderPainted(false);
        btnInventarisSupplier.setContentAreaFilled(false);
        btnInventarisSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisSupplierActionPerformed(evt);
            }
        });
        jPanel2.add(btnInventarisSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 502, 110, 20));

        btnInventarisProduk.setBorderPainted(false);
        btnInventarisProduk.setContentAreaFilled(false);
        btnInventarisProduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisProdukActionPerformed(evt);
            }
        });
        jPanel2.add(btnInventarisProduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 530, 110, 30));

        btnInventarisPesanan.setBorderPainted(false);
        btnInventarisPesanan.setContentAreaFilled(false);
        btnInventarisPesanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarisPesananActionPerformed(evt);
            }
        });
        jPanel2.add(btnInventarisPesanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 570, 110, 30));

        btnManajemenPengguna.setBorderPainted(false);
        btnManajemenPengguna.setContentAreaFilled(false);
        btnManajemenPengguna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManajemenPenggunaActionPerformed(evt);
            }
        });
        jPanel2.add(btnManajemenPengguna, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 610, 160, 60));

        btnPelaporanPenjualan.setBorderPainted(false);
        btnPelaporanPenjualan.setContentAreaFilled(false);
        btnPelaporanPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanPenjualanActionPerformed(evt);
            }
        });
        jPanel2.add(btnPelaporanPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 720, 110, 20));

        btnPelaporanInventaris.setBorderPainted(false);
        btnPelaporanInventaris.setContentAreaFilled(false);
        btnPelaporanInventaris.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanInventarisActionPerformed(evt);
            }
        });
        jPanel2.add(btnPelaporanInventaris, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 745, 110, 20));

        btnPelaporanKeuangan.setBorderPainted(false);
        btnPelaporanKeuangan.setContentAreaFilled(false);
        btnPelaporanKeuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporanKeuanganActionPerformed(evt);
            }
        });
        jPanel2.add(btnPelaporanKeuangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 770, 110, 20));

        btnLogout1.setBorderPainted(false);
        btnLogout1.setContentAreaFilled(false);
        btnLogout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogout1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnLogout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 930, 130, 40));

        btnProfilePurchase.setBorderPainted(false);
        btnProfilePurchase.setContentAreaFilled(false);
        btnProfilePurchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfilePurchaseActionPerformed(evt);
            }
        });
        jPanel2.add(btnProfilePurchase, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 180, 50));

        BG_ManajemenUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Manajemen User.png"))); // NOI18N
        jPanel2.add(BG_ManajemenUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteUserActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblPengguna.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengguna yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userIdToDelete = (int) tblPengguna.getValueAt(selectedRow, 0);
        String usernameToDelete = (String) tblPengguna.getValueAt(selectedRow, 2);

        // Pencegahan menghapus diri sendiri
        if (userIdToDelete == UserSession.getInstance().getIdPengguna()) {
            JOptionPane.showMessageDialog(this, "Anda tidak bisa menghapus akun Anda sendiri.", "Aksi Ditolak", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pengguna '" + usernameToDelete + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM PENGGUNA WHERE id_pengguna = ?";
            try (Connection conn = koneksi.getKoneksi(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, userIdToDelete);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Pengguna berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadUserData(); // Refresh tabel
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus pengguna. Pengguna tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnDeleteUserActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnTambahUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahUserActionPerformed
        // TODO add your handling code here:
        openUserFormDialog(null);
    }//GEN-LAST:event_btnTambahUserActionPerformed

    private void showAccessDeniedMessage() {
        JOptionPane.showMessageDialog(this,
                "Anda tidak memiliki hak akses untuk membuka menu ini.",
                "Akses Ditolak",
                JOptionPane.WARNING_MESSAGE);
    }

    private void btnProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        Profile profile = new Profile(this);
        profile.setVisible(true);
    }//GEN-LAST:event_btnProfileActionPerformed

    private void btnDashboard1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboard1ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        new MainMenu().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnDashboard1ActionPerformed

    private void btnEditUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditUserActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblPengguna.getSelectedRow();

        // 2. Validasi: pastikan ada baris yang dipilih.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengguna yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Ambil userId dari kolom pertama (indeks 0) dari baris yang dipilih.
        int userId = (int) tblPengguna.getValueAt(selectedRow, 0);

        // 4. Panggil helper method dengan userId tersebut untuk masuk ke mode "Edit".
        openUserFormDialog(userId);
    }//GEN-LAST:event_btnEditUserActionPerformed

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
        String peran = UserSession.getInstance().getPeran();
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
        String peranPengguna = UserSession.getInstance().getPeran();
        if (peranPengguna.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new ProdukForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnManajemenProduk1ActionPerformed

    private void btnInventarisSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisSupplierActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        String peranPengguna = UserSession.getInstance().getPeran();
        if (peranPengguna.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new SupplierForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnInventarisSupplierActionPerformed

    private void btnInventarisProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisProdukActionPerformed
        // Akses: Diizinkan untuk Staff Gudang, Manager, Administrator. Ditolak untuk Kasir.
        String peranPengguna = UserSession.getInstance().getPeran();
        if (peranPengguna.equals("Kasir")) {
            showAccessDeniedMessage();
            return;
        }
        new PurchaseOrderForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnInventarisProdukActionPerformed

    private void btnInventarisPesananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarisPesananActionPerformed
        String peranPengguna = UserSession.getInstance().getPeran();
        if (peranPengguna.equals("Kasir")) {
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
        String peranPengguna = UserSession.getInstance().getPeran();
        if (peranPengguna.equals("Kasir")) {
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

    private void btnProfilePurchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfilePurchaseActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        Profile profile = new Profile(this);
        profile.setVisible(true);
    }//GEN-LAST:event_btnProfilePurchaseActionPerformed

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
            java.util.logging.Logger.getLogger(UserManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BG_ManajemenUser;
    private javax.swing.JButton btnDashboard1;
    private javax.swing.JButton btnDeleteUser;
    private javax.swing.JButton btnEditUser;
    private javax.swing.JButton btnGantiPswd;
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
    private javax.swing.JButton btnProfilePurchase;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTambahUser;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPeran;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTable tblPengguna;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
