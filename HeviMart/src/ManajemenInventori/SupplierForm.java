package ManajemenInventori;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author Lenovo
 */
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
import Profile.Profile;
import util.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import util.UserSession;

public class SupplierForm extends javax.swing.JFrame {

    private DefaultTableModel modelTabel;
    private int selectedSupplierId = 0;
    private String namaLengkap;
    private String peran;
    private int loggedInUserId;

    /**
     * Creates new form SupplierForm
     */
    public SupplierForm() {
        initComponents();
        this.setLocationRelativeTo(null);

        UserSession session = UserSession.getInstance();
        lblUsername.setText(session.getNamaLengkap());
        lblPeran.setText(session.getPeran());

        modelTabel = (DefaultTableModel) tblSupplier.getModel();
        modelTabel.setColumnIdentifiers(new Object[]{"ID", "Nama Pemasok", "Kontak Person", "Telepon"});
        tblSupplier.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSupplier.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            // Pastikan event tidak sedang dalam proses adjustment dan ada baris yang dipilih
            if (!event.getValueIsAdjusting() && tblSupplier.getSelectedRow() != -1) {
                int selectedRow = tblSupplier.getSelectedRow();
                // Ambil ID dari kolom pertama (index 0)
                selectedSupplierId = (int) tblSupplier.getValueAt(selectedRow, 0);

                // Panggil method untuk mengisi form dengan data dari ID tersebut
                loadSupplierDataToForm(selectedSupplierId);
            }
        });

        loadSuppliers(); // Muat data awal
        clearForm();     // Siapkan form dalam mode "Tambah Baru"
    }

    private void clearForm() {
        selectedSupplierId = 0; // Kembali ke mode tambah
        txtNamaPemasok.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
        txtEmail.setText("");
        txtKontakPerson.setText("");
        tblSupplier.clearSelection();
        btnSimpan.setText("Simpan Baru");
        btnHapus.setEnabled(false); // Tombol hapus non-aktif saat mode tambah
        txtNamaPemasok.requestFocus();
    }

    private void loadSuppliers() {
        modelTabel.setRowCount(0);
        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pstmt = conn.prepareStatement("SELECT id_pemasok, nama_pemasok, kontak_person, telepon FROM PEMASOK ORDER BY nama_pemasok"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                modelTabel.addRow(new Object[]{
                    rs.getInt("id_pemasok"),
                    rs.getString("nama_pemasok"),
                    rs.getString("kontak_person"),
                    rs.getString("telepon")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pemasok: " + e.getMessage());
        }
    }

    private void showAccessDeniedMessage() {
        JOptionPane.showMessageDialog(this,
                "Anda tidak memiliki hak akses untuk membuka menu ini.",
                "Akses Ditolak",
                JOptionPane.WARNING_MESSAGE);
    }

    private void loadSupplierDataToForm(int supplierId) {
        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM PEMASOK WHERE id_pemasok = ?")) {
            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                txtNamaPemasok.setText(rs.getString("nama_pemasok"));
                txtAlamat.setText(rs.getString("alamat"));
                txtTelepon.setText(rs.getString("telepon"));
                txtEmail.setText(rs.getString("email"));
                txtKontakPerson.setText(rs.getString("kontak_person"));

                btnSimpan.setText("Update Data"); // Ubah teks tombol ke mode update
                btnHapus.setEnabled(true);      // Aktifkan tombol hapus
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat detail pemasok: " + e.getMessage());
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
        tblSupplier = new javax.swing.JTable();
        txtNamaPemasok = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtTelepon = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtKontakPerson = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        btnHapus = new javax.swing.JButton();
        btnBaru = new javax.swing.JButton();
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
        btnProfile = new javax.swing.JButton();
        BG_Supplier = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(1440, 1024));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblSupplier.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblSupplier);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 740, 1090, 220));

        txtNamaPemasok.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtNamaPemasok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaPemasokActionPerformed(evt);
            }
        });
        jPanel1.add(txtNamaPemasok, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 280, 470, 40));
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 290, -1, -1));
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 360, -1, -1));

        txtAlamat.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel1.add(txtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 350, 470, 40));

        btnSimpan.setBorderPainted(false);
        btnSimpan.setContentAreaFilled(false);
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel1.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 650, 180, 50));
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 430, -1, -1));

        txtTelepon.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtTelepon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTeleponActionPerformed(evt);
            }
        });
        jPanel1.add(txtTelepon, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 492, 470, 40));
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 580, -1, -1));

        txtKontakPerson.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtKontakPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKontakPersonActionPerformed(evt);
            }
        });
        jPanel1.add(txtKontakPerson, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 560, 470, 40));
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 500, -1, -1));

        txtEmail.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel1.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 422, 470, 40));

        btnHapus.setBorderPainted(false);
        btnHapus.setContentAreaFilled(false);
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel1.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(562, 650, 180, 50));

        btnBaru.setBorderPainted(false);
        btnBaru.setContentAreaFilled(false);
        btnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaruActionPerformed(evt);
            }
        });
        jPanel1.add(btnBaru, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 650, 180, 50));

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

        btnProfile.setBorderPainted(false);
        btnProfile.setContentAreaFilled(false);
        btnProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileActionPerformed(evt);
            }
        });
        jPanel1.add(btnProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 180, 50));

        BG_Supplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Supplier.png"))); // NOI18N
        jPanel1.add(BG_Supplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNamaPemasokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaPemasokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaPemasokActionPerformed

    private void txtTeleponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTeleponActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTeleponActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:

        String nama = txtNamaPemasok.getText();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Pemasok tidak boleh kosong.");
            return;
        }

        try (Connection conn = koneksi.getKoneksi()) {
            PreparedStatement pstmt;
            if (selectedSupplierId == 0) { // Mode Tambah Baru
                String sql = "INSERT INTO PEMASOK (nama_pemasok, alamat, telepon, email, kontak_person) VALUES (?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
            } else { // Mode Update
                String sql = "UPDATE PEMASOK SET nama_pemasok=?, alamat=?, telepon=?, email=?, kontak_person=? WHERE id_pemasok=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(6, this.selectedSupplierId);
            }
            pstmt.setString(1, nama);
            pstmt.setString(2, txtAlamat.getText());
            pstmt.setString(3, txtTelepon.getText());
            pstmt.setString(4, txtEmail.getText());
            pstmt.setString(5, txtKontakPerson.getText());
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data pemasok berhasil disimpan!");
            loadSuppliers(); // Muat ulang tabel
            clearForm();     // Bersihkan form
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        if (selectedSupplierId == 0) {
            JOptionPane.showMessageDialog(this, "Pilih pemasok yang akan dihapus dari tabel.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus pemasok ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = koneksi.getKoneksi(); PreparedStatement pstmt = conn.prepareStatement("DELETE FROM PEMASOK WHERE id_pemasok = ?")) {
                pstmt.setInt(1, selectedSupplierId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Pemasok berhasil dihapus!");
                loadSuppliers();
                clearForm();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_btnBaruActionPerformed

    private void txtKontakPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKontakPersonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKontakPersonActionPerformed

    private void btnManajemenProduk1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnManajemenProduk1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnManajemenProduk1MouseClicked

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
        JOptionPane.showMessageDialog(this, "Anda sudah berada di halaman Supplier.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
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

    private void btnProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        Profile profile = new Profile(this);
        profile.setVisible(true);
    }//GEN-LAST:event_btnProfileActionPerformed

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
            java.util.logging.Logger.getLogger(SupplierForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SupplierForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SupplierForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SupplierForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SupplierForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BG_Supplier;
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnDashboard1;
    private javax.swing.JButton btnHapus;
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
    private javax.swing.JButton btnSimpan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPeran;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTable tblSupplier;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtKontakPerson;
    private javax.swing.JTextField txtNamaPemasok;
    private javax.swing.JTextField txtTelepon;
    // End of variables declaration//GEN-END:variables
}
