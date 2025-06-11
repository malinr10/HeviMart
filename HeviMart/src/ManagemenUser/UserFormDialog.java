/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ManagemenUser;

import model.User;
import dao.UserDAO;
import util.PasswordUtil; // Untuk hashing password
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserFormDialog extends javax.swing.JDialog {

    private boolean isEditMode;
    private int userIdToEdit;
    private boolean dataSaved = false;
    
    public UserFormDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal); // Panggil konstruktor JDialog
        this.setTitle("Tambah Pengguna Baru");
        this.isEditMode = false; // Set mode ke "Tambah"
        initComponents(); // Panggil initComponents untuk inisialisasi GUI
        setupRolesComboBox(); // Setup ComboBox role
        this.setLocationRelativeTo(parent); // Posisikan dialog di tengah parent

        // Atur visibilitas dan editable sesuai mode "Tambah"
        lblPassword.setVisible(true);
        txtPassword.setVisible(true);
        lblConfirmPassword.setVisible(true);
        txtConfirmPassword.setVisible(true);
        txtUsername.setEditable(true); // Username bisa diedit di mode tambah

        // Sembunyikan label ID di mode tambah (jika ada di desain)
        if (jLabel2 != null) jLabel2.setVisible(false); // Label "ID user:"
        if (jLabel10 != null) jLabel10.setVisible(false); // Label yang menampilkan ID
    }

    // --- Konstruktor 2: Untuk mode Edit Pengguna ---
    public UserFormDialog(java.awt.Frame parent, boolean modal, int userId) {
        super(parent, modal); // Panggil konstruktor JDialog
        this.setTitle("Edit Pengguna");
        this.isEditMode = true; // Set mode ke "Edit"
        this.userIdToEdit = userId; // Simpan ID pengguna yang akan diedit
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
        if (jLabel2 != null) jLabel2.setVisible(true);
        if (jLabel10 != null) jLabel10.setVisible(true);
    }
    
     private void setupRolesComboBox() {
        cmbRole.addItem("Admin");
        cmbRole.addItem("Manager");
        cmbRole.addItem("Kasir");
        cmbRole.addItem("Staff Gudang");
        // Pilih default
        cmbRole.setSelectedIndex(0);
    }

    private void loadUserDataForEdit(int userId) {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserById(userId);
        if (user != null) {
            if (jLabel10 != null) jLabel10.setText(String.valueOf(user.getIdPengguna())); // Tampilkan ID Pengguna
            txtNamaLengkap.setText(user.getNamaLengkap());
            txtUsername.setText(user.getNamaPengguna());
            txtEmail.setText(user.getEmail());
            txtTelepon.setText(user.getTelepon());
            cmbRole.setSelectedItem(user.getPeran());
        } else {
            JOptionPane.showMessageDialog(this, "Data pengguna tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void saveUserData() {
        // Logika saveUserData yang kompleks (sudah saya berikan sebelumnya)
        // ...
        String namaLengkap = txtNamaLengkap.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String telepon = txtTelepon.getText().trim();
        String peran = (String) cmbRole.getSelectedItem();

        // Validasi
        if (namaLengkap.isEmpty() || username.isEmpty() || peran.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap, Username, dan Peran tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!isEditMode) { // Validasi password hanya di mode tambah
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
        }

        User user = new User();
        if (isEditMode) {
            user.setIdPengguna(userIdToEdit);
        }
        user.setNamaLengkap(namaLengkap);
        user.setNamaPengguna(username);
        user.setPeran(peran);
        user.setEmail(email);
        user.setTelepon(telepon);

        UserDAO userDAO = new UserDAO();
        boolean success = false;

        if (isEditMode) {
            success = userDAO.updateUser(user);
        } else {
            String plainPassword = String.valueOf(txtPassword.getPassword());
            success = userDAO.addUser(user, plainPassword);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Data pengguna berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dataSaved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data pengguna. Mungkin username sudah ada.", "Error", JOptionPane.ERROR_MESSAGE);
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
        lblJudul = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblConfirmPassword = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        txtNamaLengkap = new javax.swing.JTextField();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        txtConfirmPassword = new javax.swing.JPasswordField();
        cmbRole = new javax.swing.JComboBox<>();
        txtEmail = new javax.swing.JTextField();
        txtTelepon = new javax.swing.JTextField();
        btnBatal = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        BG_InputData = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(896, 796));
        setSize(new java.awt.Dimension(896, 790));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblJudul.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 48)); // NOI18N
        lblJudul.setForeground(new java.awt.Color(255, 255, 255));
        lblJudul.setText("Form Edit Data");
        jPanel1.add(lblJudul, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 33, -1, -1));
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 190, -1, -1));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 183, 320, 30));
        jPanel1.add(lblConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 450, 110, 20));
        jPanel1.add(lblPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 380, 110, 20));

        txtNamaLengkap.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtNamaLengkap.setBorder(null);
        jPanel1.add(txtNamaLengkap, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 250, 320, 30));

        txtUsername.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtUsername.setBorder(null);
        jPanel1.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 312, 320, 30));

        txtPassword.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtPassword.setBorder(null);
        jPanel1.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 377, 320, 30));

        txtConfirmPassword.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtConfirmPassword.setBorder(null);
        jPanel1.add(txtConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 440, 320, 30));

        cmbRole.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Manager", "Kasir", "Staff Gudang", " " }));
        cmbRole.setBorder(null);
        jPanel1.add(cmbRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(402, 500, 350, 40));

        txtEmail.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtEmail.setBorder(null);
        jPanel1.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(421, 567, 320, 30));

        txtTelepon.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtTelepon.setBorder(null);
        jPanel1.add(txtTelepon, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 632, 320, 30));

        btnBatal.setBorderPainted(false);
        btnBatal.setContentAreaFilled(false);
        jPanel1.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(232, 710, 160, 40));

        btnSimpan.setBorderPainted(false);
        btnSimpan.setContentAreaFilled(false);
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel1.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 710, 170, 40));

        BG_InputData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/MU_InputData (1).png"))); // NOI18N
        jPanel1.add(BG_InputData, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
    }//GEN-LAST:event_btnSimpanActionPerformed

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
    private javax.swing.JLabel BG_InputData;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNamaLengkap;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtTelepon;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
