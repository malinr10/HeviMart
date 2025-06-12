/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ManagemenUser;

import util.PasswordUtil; // Untuk hashing password
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserFormDialog extends javax.swing.JDialog {

    private boolean isEditMode;
    private int userIdToEdit;
    private boolean dataSaved = false;
    private UserDAO userDAO;

    // --- Konstruktor 1: Untuk mode Tambah Pengguna Baru ---
    public UserFormDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal); // Panggil konstruktor JDialog
        this.setTitle("Tambah Pengguna Baru");
        this.isEditMode = false; // Set mode ke "Tambah"
        userDAO = new UserDAO(); // Inisialisasi UserDAO
        initComponents(); // Panggil initComponents untuk inisialisasi GUI
        setupRolesComboBox(); // Setup ComboBox role
        this.setLocationRelativeTo(parent); // Posisikan dialog di tengah parent

        // Atur visibilitas dan editable sesuai mode "Tambah"
        lblPassword.setVisible(true);
        txtPassword.setVisible(true);
        lblConfirmPassword.setVisible(true);
        txtConfirmPassword.setVisible(true);
        // txtUsername.setEditable(true); // Ini sudah default JTextField

        // Sembunyikan label ID di mode tambah
        IDUser.setVisible(false); // Label "ID user:"
        ID.setVisible(false); // Label yang menampilkan ID kosong

        // Tambahkan ActionListener untuk tombol Batal
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Tutup dialog
            }
        });
    }

    // --- Konstruktor 2: Untuk mode Edit Pengguna ---
    public UserFormDialog(java.awt.Frame parent, boolean modal, int userId) {
        super(parent, modal); // Panggil konstruktor JDialog
        this.setTitle("Edit Pengguna");
        this.isEditMode = true; // Set mode ke "Edit"
        this.userIdToEdit = userId; // Simpan ID pengguna yang akan diedit
        userDAO = new UserDAO(); // Inisialisasi UserDAO
        initComponents(); // Panggil initComponents untuk inisialisasi GUI
        setupRolesComboBox(); // Setup ComboBox role
        loadUserDataForEdit(userId); // Muat data pengguna yang akan diedit
        this.setLocationRelativeTo(parent); // Posisikan dialog di tengah parent

        // Sembunyikan field password di mode edit
        lblPassword.setVisible(false);
        txtPassword.setVisible(false);
        lblConfirmPassword.setVisible(false);
        txtConfirmPassword.setVisible(false);
        txtUsername.setEditable(false); // Username tidak bisa diedit di mode edit

        // Pastikan label ID terlihat di mode edit
        IDUser.setVisible(true);
        ID.setVisible(true);

        // Tambahkan ActionListener untuk tombol Batal
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Tutup dialog
            }
        });
    }

    // Metode untuk mengisi JComboBox peran
    private void setupRolesComboBox() {
        cmbRole.removeAllItems(); // Bersihkan item yang mungkin ada dari desainer
        cmbRole.addItem("Administrator");
        cmbRole.addItem("Manager");
        cmbRole.addItem("Kasir");
        cmbRole.addItem("Staff Gudang");
        // Pilih default (opsional, bisa disesuaikan)
        // cmbRole.setSelectedIndex(0); 
    }

    // Metode untuk memuat data pengguna dari database ke form (untuk mode edit)
    private void loadUserDataForEdit(int userId) {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            ID.setText(String.valueOf(user.getIdPengguna())); // Tampilkan ID Pengguna
            txtNamaLengkap.setText(user.getNamaLengkap());
            txtUsername.setText(user.getNamaPengguna());
            txtEmail.setText(user.getEmail());
            txtTelepon.setText(user.getTelepon());
            cmbRole.setSelectedItem(user.getPeran());
            // Jika Anda memiliki JCheckBox 'aktif' di desain Anda, tambahkan ini:
            // chkAktif.setSelected(user.isAktif()); 
        } else {
            JOptionPane.showMessageDialog(this, "Data pengguna tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    // Metode untuk menyimpan data pengguna (baik tambah maupun edit)
    private void saveUserData() {
        String namaLengkap = txtNamaLengkap.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String telepon = txtTelepon.getText().trim();
        String peran = (String) cmbRole.getSelectedItem();
        // Asumsi ada JCheckBox chkAktif untuk status aktif
        // boolean aktif = chkAktif.isSelected(); 
        boolean aktif = true; // Default true jika tidak ada chkAktif di desain

        // Validasi
        if (namaLengkap.isEmpty() || username.isEmpty() || peran.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap, Username, dan Peran tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User();
        user.setNamaLengkap(namaLengkap);
        user.setNamaPengguna(username);
        user.setPeran(peran);
        user.setEmail(email);
        user.setTelepon(telepon);
        user.setAktif(aktif); // Set status aktif

        boolean success = false;

        try {
            if (isEditMode) {
                user.setIdPengguna(userIdToEdit); // Penting: set ID untuk update
                // Cek duplikasi username/email untuk user lain saat edit
                if (userDAO.isUsernameOrEmailExists(username, email, userIdToEdit)) {
                    JOptionPane.showMessageDialog(this, "Username atau Email sudah digunakan oleh pengguna lain.", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                success = userDAO.updateUser(user);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Data pengguna berhasil diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui data pengguna.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else { // Mode Tambah
                char[] passwordChars = txtPassword.getPassword();
                char[] confirmPasswordChars = txtConfirmPassword.getPassword();

                if (passwordChars.length == 0 || confirmPasswordChars.length == 0) {
                    JOptionPane.showMessageDialog(this, "Password tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!String.valueOf(passwordChars).equals(String.valueOf(confirmPasswordChars))) {
                    JOptionPane.showMessageDialog(this, "Password dan konfirmasi password tidak cocok.", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Cek duplikasi username/email sebelum menambah
                if (userDAO.isUsernameOrEmailExists(username, email, -1)) { // -1 karena ini pengguna baru
                    JOptionPane.showMessageDialog(this, "Username atau Email sudah terdaftar.", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String plainPassword = String.valueOf(txtPassword.getPassword());
                success = userDAO.addUser(user, plainPassword); // Menggunakan UserDAO untuk menambahkan user
                if (success) {
                    JOptionPane.showMessageDialog(this, "Pengguna baru berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan pengguna. Periksa log database untuk detail.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (success) {
                dataSaved = true;
                this.dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Ini juga harus ada, dipanggil oleh UserManagementPanel
    public boolean isDataSaved() {
        return dataSaved;
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
        labelForm = new javax.swing.JLabel();
        IDUser = new javax.swing.JLabel();
        lblNamaLengkap = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        lblConfirmPassword = new javax.swing.JLabel();
        lblRole = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblTelepon = new javax.swing.JLabel();
        ID = new javax.swing.JLabel();
        txtNamaLengkap = new javax.swing.JTextField();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        txtConfirmPassword = new javax.swing.JPasswordField();
        cmbRole = new javax.swing.JComboBox<>();
        txtEmail = new javax.swing.JTextField();
        txtTelepon = new javax.swing.JTextField();
        btnBatal = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        BG_TambahData = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelForm.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 48)); // NOI18N
        labelForm.setForeground(new java.awt.Color(255, 255, 255));
        labelForm.setText("Form Tambah Data");
        jPanel1.add(labelForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, -1, -1));
        jPanel1.add(IDUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 190, 70, 10));
        jPanel1.add(lblNamaLengkap, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 260, -1, -1));
        jPanel1.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 320, -1, -1));
        jPanel1.add(lblPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 380, -1, -1));
        jPanel1.add(lblConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 450, -1, -1));
        jPanel1.add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 510, -1, -1));
        jPanel1.add(lblEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 570, -1, -1));
        jPanel1.add(lblTelepon, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 640, -1, -1));

        ID.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel1.add(ID, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 185, 320, 30));

        txtNamaLengkap.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtNamaLengkap.setBorder(null);
        txtNamaLengkap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaLengkapActionPerformed(evt);
            }
        });
        jPanel1.add(txtNamaLengkap, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 247, 320, 30));

        txtUsername.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtUsername.setBorder(null);
        jPanel1.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 312, 320, 30));

        txtPassword.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtPassword.setBorder(null);
        jPanel1.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 377, 320, 30));

        txtConfirmPassword.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtConfirmPassword.setBorder(null);
        txtConfirmPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtConfirmPasswordActionPerformed(evt);
            }
        });
        jPanel1.add(txtConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 440, 320, 30));

        cmbRole.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        cmbRole.setBorder(null);
        cmbRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRoleActionPerformed(evt);
            }
        });
        jPanel1.add(cmbRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 500, 350, 40));

        txtEmail.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtEmail.setBorder(null);
        jPanel1.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 570, 320, 30));

        txtTelepon.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtTelepon.setBorder(null);
        jPanel1.add(txtTelepon, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 632, 320, 30));

        btnBatal.setBorderPainted(false);
        btnBatal.setContentAreaFilled(false);
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        jPanel1.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 710, 160, 40));

        btnSimpan.setBorderPainted(false);
        btnSimpan.setContentAreaFilled(false);
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel1.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 710, 170, 40));

        BG_TambahData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/MU_InputData (1).png"))); // NOI18N
        jPanel1.add(BG_TambahData, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
        saveUserData();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void txtNamaLengkapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaLengkapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaLengkapActionPerformed

    private void txtConfirmPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConfirmPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtConfirmPasswordActionPerformed

    private void cmbRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRoleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbRoleActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

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
            java.util.logging.Logger.getLogger(UserFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserFormDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UserFormDialog dialog = new UserFormDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BG_TambahData;
    private javax.swing.JLabel ID;
    private javax.swing.JLabel IDUser;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelForm;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblNamaLengkap;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblTelepon;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNamaLengkap;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtTelepon;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
