/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ManagemenUser;

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

    public UserManagement(int loggedInUserId, String loggedInUserRole) { // Constructor menerima data user login
        this.currentLoggedInUserId = loggedInUserId;
        this.currentLoggedInUserRole = loggedInUserRole;
        
        initComponents();
        this.setLocationRelativeTo(null); // Posisikan di tengah layar

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
        checkUserRolePermissions(); // Periksa izin berdasarkan peran pengguna
        
        // Tambahkan listener untuk live search
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchAndLoadUserData(); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchAndLoadUserData(); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchAndLoadUserData(); }
        });

        // Tambahkan event listeners untuk tombol-tombol
        addEventListeners();
    }
    public UserManagement() {
        this(1, "Admin"); // Default untuk testing
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
    private void checkUserRolePermissions() {
        if (!currentLoggedInUserRole.equalsIgnoreCase("Admin")) {
            btnTambahUser.setEnabled(false);
            btnEditUser.setEnabled(false);
            btnGantiPswd.setEnabled(false);
            btnDeleteUser.setEnabled(false);
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

        btnGantiPswd = new javax.swing.JButton();
        btnEditUser = new javax.swing.JButton();
        btnDeleteUser = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnTambahUser = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPengguna = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnGantiPswd.setText("editpswd");

        btnEditUser.setText("edituser");

        btnDeleteUser.setText("delete");
        btnDeleteUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteUserActionPerformed(evt);
            }
        });

        jLabel1.setText("User Managemenrt");

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        jLabel2.setText("Cari");

        btnTambahUser.setText("tambah");
        btnTambahUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahUserActionPerformed(evt);
            }
        });

        jLabel3.setText("User Detail");

        btnRefresh.setText("refresh");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnGantiPswd)
                        .addGap(76, 76, 76)
                        .addComponent(btnEditUser)
                        .addGap(73, 73, 73)
                        .addComponent(btnDeleteUser))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnRefresh))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnTambahUser))
                        .addComponent(jLabel1)))
                .addContainerGap(84, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnTambahUser))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(btnRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGantiPswd)
                    .addComponent(btnEditUser)
                    .addComponent(btnDeleteUser))
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteUserActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnTambahUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahUserActionPerformed

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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserManagement(1, "Admin").setVisible(true); 
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteUser;
    private javax.swing.JButton btnEditUser;
    private javax.swing.JButton btnGantiPswd;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTambahUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPengguna;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
