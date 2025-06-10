/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ManagemenUser;

import dao.UserDAO;
import model.User;
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
    
    public UserManagement() {
        initComponents();
        
        modelTabelPengguna = new DefaultTableModel();
        tblPengguna.setModel(modelTabelPengguna);
        modelTabelPengguna.addColumn("ID");
        modelTabelPengguna.addColumn("Nama Lengkap");
        modelTabelPengguna.addColumn("Username");
        modelTabelPengguna.addColumn("Role");
        modelTabelPengguna.addColumn("Email");
        modelTabelPengguna.addColumn("Telepon");
        modelTabelPengguna.addColumn("Aktif");

        // Muat data saat panel pertama kali dibuka
        loadUserData();

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
                    int confirm = JOptionPane.showConfirmDialog(UserManagement.this, "Apakah Anda yakin ingin menghapus pengguna ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int userId = (int) modelTabelPengguna.getValueAt(selectedRow, 0);
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
                loadUserData(); // Muat ulang semua data
            }
        });
    }

    // Metode untuk memuat data pengguna dari database (tanpa filter/search)
    private void loadUserData() {
        modelTabelPengguna.setRowCount(0); // Kosongkan tabel
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers(); // Panggil method dari UserDAO
        for (User user : users) {
            modelTabelPengguna.addRow(new Object[]{
                user.getIdPengguna(),
                user.getNamaLengkap(),
                user.getNamaPengguna(),
                user.getPeran(),
                user.getEmail(),
                user.getTelepon(),
            });
        }
    }

    // Metode untuk memuat data pengguna dari database dengan filter/search
    private void searchAndLoadUserData() {
        String keyword = txtSearch.getText();
        if (keyword.isEmpty()) {
            loadUserData(); // Jika keyword kosong, tampilkan semua data
            return;
        }

        modelTabelPengguna.setRowCount(0); // Kosongkan tabel
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.searchUsers(keyword); // Anda perlu menambahkan metode searchUsers di UserDAO
        for (User user : users) {
            modelTabelPengguna.addRow(new Object[]{
                user.getIdPengguna(),
                user.getNamaLengkap(),
                user.getNamaPengguna(),
                user.getPeran(),
                user.getEmail(),
                user.getTelepon(),
            });
        }
    }

    // Metode untuk menampilkan dialog tambah/edit pengguna
    // Metode untuk menampilkan dialog tambah/edit pengguna
    private void openUserFormDialog(Integer userId) {
        // Tidak perlu lagi mendapatkan parentWindow via SwingUtilities.getWindowAncestor
        UserFormDialog dialog;
        if (userId == null) {
            // 'this' di sini adalah instance dari UserManagement (yang adalah JFrame)
            dialog = new UserFormDialog(this, true); // True untuk modal dialog
        } else {
            dialog = new UserFormDialog(this, true, userId);
        }
        dialog.setVisible(true);
        if (dialog.isDataSaved()) {
            loadUserData();
        }
    }

    // Metode untuk menampilkan dialog ganti password
    private void openChangePasswordDialog(int userId) {
    // Tidak perlu lagi mendapatkan parentWindow via SwingUtilities.getWindowAncestor
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, true, userId);
        dialog.setVisible(true);
    // Jika password berhasil diubah, tidak perlu reload tabel penuh, hanya feedback ke user
    }

    // Metode untuk menghapus pengguna
    private void deleteUser(int userId) {
        UserDAO userDAO = new UserDAO();
        if (userDAO.deleteUser(userId)) {
            JOptionPane.showMessageDialog(this, "Pengguna berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadUserData(); // Muat ulang tabel
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus pengguna.", "Error", JOptionPane.ERROR_MESSAGE);
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

        txtSearch.setText("search");
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        jLabel2.setText("icon");

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
                .addContainerGap(82, Short.MAX_VALUE))
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserManagement().setVisible(true);
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
