package PosSistem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
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

import ManajemenInventori.ProductSearchDialog;
import util.koneksi;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lenovo
 */
public class POSForm extends javax.swing.JFrame {

    private DefaultTableModel modelTabel;
    private final int ID_KASIR;
    private BigDecimal grandTotal = BigDecimal.ZERO;
    private String namaLengkap;
    private String peran;

    /**
     * Creates new form POSForm
     */

    public POSForm() {
        initComponents();
        this.setLocationRelativeTo(null);
        
        // FIX 3: Logika inisialisasi dipindahkan ke constructor yang benar
        // Ambil data dari session
        UserSession session = UserSession.getInstance();
        this.ID_KASIR = session.getIdPengguna(); // Ambil ID kasir dari sesi
        String namaLengkap = session.getNamaLengkap();
        String peran = session.getPeran();
        this.setTitle("SuperMart POS - Kasir ID: " + ID_KASIR);
        lblUsername.setText(this.namaLengkap);
        lblPeran.setText(this.peran);

        // Setup Tabel Keranjang Belanja
        modelTabel = (DefaultTableModel) tblShoppingCart.getModel();
        modelTabel.setColumnIdentifiers(new Object[]{"ID", "Nama Produk", "Harga", "Jml", "Diskon", "Subtotal"});

        // Listener untuk input barcode (saat tombol Enter ditekan)
        txtBarcode.addActionListener((ActionEvent e) -> {
            tambahProdukKeKeranjang(txtBarcode.getText());
        });

        // Listener untuk menghitung kembalian secara real-time
        txtJumlahBayar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateKembalian();
            }
        });

        clearForm();

    }

    private void tambahProdukKeKeranjang(String barcode) {
        if (barcode.trim().isEmpty()) {
            return;
        }

        // Query SQL yang canggih: Mengambil data produk sekaligus diskon aktif yang berlaku hari ini
        String sql = "SELECT p.id_produk, p.nama_produk, p.harga_jual, d.tipe_diskon, d.nilai AS nilai_diskon "
                + "FROM PRODUK p "
                + "LEFT JOIN DISKON_PRODUK dp ON p.id_produk = dp.id_produk "
                + "LEFT JOIN DISKON d ON dp.id_diskon = d.id_diskon "
                + "AND d.aktif = TRUE AND CURDATE() BETWEEN d.mulai_berlaku AND d.akhir_berlaku "
                + "WHERE p.kode_barcode = ? LIMIT 1";

        try (Connection conn = koneksi.getKoneksi(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barcode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int idProduk = rs.getInt("id_produk");
                String nama = rs.getString("nama_produk");
                BigDecimal harga = rs.getBigDecimal("harga_jual");
                String tipeDiskon = rs.getString("tipe_diskon");
                BigDecimal nilaiDiskon = rs.getBigDecimal("nilai_diskon");

                // --- Logika Perhitungan Diskon ---
                BigDecimal diskonPerItem = BigDecimal.ZERO;
                if (tipeDiskon != null) {
                    if (tipeDiskon.equals("persen")) {
                        // Hitung diskon persentase: harga * (nilai_diskon / 100)
                        diskonPerItem = harga.multiply(nilaiDiskon.divide(new BigDecimal(100)));
                    } else if (tipeDiskon.equals("nominal")) {
                        // Ambil langsung nilai diskon nominal
                        diskonPerItem = nilaiDiskon;
                    }
                }

                // Cek apakah produk sudah ada di keranjang untuk update jumlah
                for (int i = 0; i < modelTabel.getRowCount(); i++) {
                    if ((int) modelTabel.getValueAt(i, 0) == idProduk) {
                        int qtyLama = (int) modelTabel.getValueAt(i, 3);
                        int qtyBaru = qtyLama + 1;

                        BigDecimal totalDiskon = diskonPerItem.multiply(new BigDecimal(qtyBaru));
                        BigDecimal subtotal = harga.multiply(new BigDecimal(qtyBaru)).subtract(totalDiskon);

                        modelTabel.setValueAt(qtyBaru, i, 3); // Update jumlah
                        modelTabel.setValueAt(totalDiskon, i, 4); // Update diskon
                        modelTabel.setValueAt(subtotal, i, 5); // Update subtotal

                        updateTotal();
                        txtBarcode.setText("");
                        return;
                    }
                }

                // Jika produk baru, tambahkan baris baru
                BigDecimal subtotal = harga.subtract(diskonPerItem);
                modelTabel.addRow(new Object[]{idProduk, nama, harga, 1, diskonPerItem, subtotal});
                updateTotal();

            } else {
                JOptionPane.showMessageDialog(this, "Produk dengan barcode '" + barcode + "' tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            txtBarcode.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saat mencari produk: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTotal() {
        BigDecimal subtotalKotor = BigDecimal.ZERO;
        BigDecimal totalDiskon = BigDecimal.ZERO;

        for (int i = 0; i < modelTabel.getRowCount(); i++) {
            BigDecimal harga = (BigDecimal) modelTabel.getValueAt(i, 2);
            int jumlah = (int) modelTabel.getValueAt(i, 3);
            BigDecimal diskon = (BigDecimal) modelTabel.getValueAt(i, 4);

            subtotalKotor = subtotalKotor.add(harga.multiply(new BigDecimal(jumlah)));
            totalDiskon = totalDiskon.add(diskon);
        }

        // Simpan hasil perhitungan Grand Total ke variabel kelas
        this.grandTotal = subtotalKotor.subtract(totalDiskon);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        // Pastikan Anda punya JLabel lblSubtotal, lblDiskon, dan lblGrandTotal di form
        lblSubtotal.setText(formatRupiah.format(subtotalKotor));
        lblDiskon.setText(formatRupiah.format(totalDiskon));
        lblGrandTotal.setText(formatRupiah.format(this.grandTotal));

        updateKembalian();
    }

    private void updateKembalian() {
        try {
            BigDecimal jumlahBayar = new BigDecimal(txtJumlahBayar.getText());
            // Gunakan variabel this.grandTotal yang sudah pasti benar
            BigDecimal kembalian = jumlahBayar.subtract(this.grandTotal);

            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            lblKembalian.setText(formatRupiah.format(kembalian));
        } catch (Exception e) {
            lblKembalian.setText("Rp 0");
        }
    }

    private void clearForm() {
        modelTabel.setRowCount(0);
        txtBarcode.setText("");
        txtJumlahBayar.setText("");
        updateTotal();
        txtBarcode.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblShoppingCart = new javax.swing.JTable();
        lblSubtotal = new javax.swing.JLabel();
        lblDiskon = new javax.swing.JLabel();
        lblGrandTotal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        cmbMetodeBayar = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtJumlahBayar = new javax.swing.JTextField();
        lblKembalian = new javax.swing.JLabel();
        lblJudul = new javax.swing.JTextField();
        btnBayar = new javax.swing.JButton();
        btnHapusItem = new javax.swing.JButton();
        btnBatalTransaksi = new javax.swing.JButton();
        btnRiwayat = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        lblPeran = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        btnProfile = new javax.swing.JButton();
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
        BG_POSForm = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout(0, 1));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 280, 50, 40));

        txtBarcode.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtBarcode.setBorder(null);
        txtBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBarcodeActionPerformed(evt);
            }
        });
        jPanel2.add(txtBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 285, 290, 40));

        tblShoppingCart.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblShoppingCart);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 370, 690, 500));

        lblSubtotal.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel2.add(lblSubtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 280, 180, 40));

        lblDiskon.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel2.add(lblDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 340, 180, 40));

        lblGrandTotal.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel2.add(lblGrandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 406, 180, 40));
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 480, -1, -1));
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        cmbMetodeBayar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        cmbMetodeBayar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tunai", "QRIS", "Kartu Debit", "Kartu Kredit" }));
        cmbMetodeBayar.setBorder(null);
        jPanel2.add(cmbMetodeBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 510, 290, 60));
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 590, -1, -1));

        txtJumlahBayar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtJumlahBayar.setBorder(null);
        jPanel2.add(txtJumlahBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 633, 260, 40));

        lblKembalian.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPanel2.add(lblKembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 748, 260, 40));

        lblJudul.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblJudul.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lblJudul.setText("Pembayaran");
        lblJudul.setBorder(null);
        lblJudul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblJudulActionPerformed(evt);
            }
        });
        jPanel2.add(lblJudul, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 470, 310, -1));

        btnBayar.setBorderPainted(false);
        btnBayar.setContentAreaFilled(false);
        btnBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBayarActionPerformed(evt);
            }
        });
        jPanel2.add(btnBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 830, 280, 50));

        btnHapusItem.setBorderPainted(false);
        btnHapusItem.setContentAreaFilled(false);
        btnHapusItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusItemActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapusItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(1321, 173, 60, 50));

        btnBatalTransaksi.setBorderPainted(false);
        btnBatalTransaksi.setContentAreaFilled(false);
        btnBatalTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalTransaksiActionPerformed(evt);
            }
        });
        jPanel2.add(btnBatalTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(897, 173, 60, 50));

        btnRiwayat.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnRiwayat.setText("Riwayat Transaksi");
        btnRiwayat.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnRiwayat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatActionPerformed(evt);
            }
        });
        jPanel2.add(btnRiwayat, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 883, 150, 40));

        btnCari.setBorderPainted(false);
        btnCari.setContentAreaFilled(false);
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        jPanel2.add(btnCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 290, 60, 50));

        lblPeran.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblPeran.setForeground(new java.awt.Color(30, 41, 59));
        lblPeran.setText("peran");
        jPanel2.add(lblPeran, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 120, 20));

        lblUsername.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(30, 41, 59));
        lblUsername.setText("username");
        jPanel2.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, 120, 20));

        btnProfile.setBorderPainted(false);
        btnProfile.setContentAreaFilled(false);
        jPanel2.add(btnProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 180, 50));

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
        jPanel2.add(btnPelaporanKeuangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 770, 70, 20));

        btnLogout1.setBorderPainted(false);
        btnLogout1.setContentAreaFilled(false);
        btnLogout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogout1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnLogout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 930, 130, 40));

        BG_POSForm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/POS.png"))); // NOI18N
        jPanel2.add(BG_POSForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBarcodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBarcodeActionPerformed

    private void btnHapusItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusItemActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblShoppingCart.getSelectedRow();
        if (selectedRow != -1) {
            modelTabel.removeRow(selectedRow);
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih item di keranjang yang akan dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnHapusItemActionPerformed

    private void btnBatalTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalTransaksiActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(this, "Anda yakin ingin membatalkan transaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            clearForm();
        }
    }//GEN-LAST:event_btnBatalTransaksiActionPerformed

    private void btnRiwayatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatActionPerformed
        // TODO add your handling code here:
        new TransactionHistoryForm(this, true).setVisible(true);
    }//GEN-LAST:event_btnRiwayatActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
        ManajemenInventori.ProductSearchDialog dialog = new ManajemenInventori.ProductSearchDialog(this, true);
        dialog.setVisible(true); // Program akan menunggu di sini sampai dialog ditutup

        // 2. Setelah dialog ditutup, cek apakah ada barcode yang dipilih
        if (dialog.selectedProductBarcode != null && !dialog.selectedProductBarcode.isEmpty()) {
            // 3. Jika ada, panggil method untuk menambahkannya ke keranjang
            // Seolah-olah barcode tersebut di-scan
            tambahProdukKeKeranjang(dialog.selectedProductBarcode);
        }

        // Setelah dialog ditutup, ambil data dari variabel publiknya

    }//GEN-LAST:event_btnCariActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        // TODO add your handling code here:
        // === VALIDASI SEBELUM BAYAR ===
        if (modelTabel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong.");
            return;
        }

        String bayarString = txtJumlahBayar.getText();
        if (bayarString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah pembayaran terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtJumlahBayar.requestFocus();
            return;
        }

        BigDecimal jumlahBayar;
        try {
            jumlahBayar = new BigDecimal(bayarString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format jumlah bayar tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (jumlahBayar.compareTo(this.grandTotal) < 0) {
            JOptionPane.showMessageDialog(this, "Jumlah pembayaran kurang dari Grand Total.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // === PROSES TRANSAKSI ===
        Connection conn = null;
        try {
            conn = koneksi.getKoneksi();
            conn.setAutoCommit(false);

            // Ambil semua data yang akan disimpan
            String metodeBayar = (String) cmbMetodeBayar.getSelectedItem();
            BigDecimal kembalian = jumlahBayar.subtract(this.grandTotal);
            BigDecimal totalSebelumDiskon = this.grandTotal.add((BigDecimal) new BigDecimal(lblDiskon.getText().replaceAll("[^\\d,]", "").replace(",", ".")));

            // Query INSERT yang sudah lengkap
            String sqlTrx = "INSERT INTO TRANSAKSI (kode_transaksi, id_kasir, total_harga, diskon, harga_akhir, metode_bayar, jumlah_bayar, kembalian) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtTrx = conn.prepareStatement(sqlTrx, Statement.RETURN_GENERATED_KEYS);

            String kodeTrx = "TRX-" + System.currentTimeMillis();
            pstmtTrx.setString(1, kodeTrx);
            pstmtTrx.setInt(2, this.ID_KASIR);
            pstmtTrx.setBigDecimal(3, totalSebelumDiskon);
            pstmtTrx.setBigDecimal(4, new BigDecimal(lblDiskon.getText().replaceAll("[^\\d,]", "").replace(",", ".")));
            pstmtTrx.setBigDecimal(5, this.grandTotal);
            pstmtTrx.setString(6, metodeBayar);
            pstmtTrx.setBigDecimal(7, jumlahBayar);
            pstmtTrx.setBigDecimal(8, kembalian);

            pstmtTrx.executeUpdate();

            // ... (sisa kode untuk menyimpan DETAIL_TRANSAKSI, UPDATE PRODUK, dan INSERT PERGERAKAN_STOK tetap sama persis seperti sebelumnya) ...
            ResultSet rsKeys = pstmtTrx.getGeneratedKeys();
            int trxId = rsKeys.next() ? rsKeys.getInt(1) : 0;
            if (trxId == 0) {
                throw new SQLException("Gagal membuat transaksi utama.");
            }

            // ... (Loop for untuk menyimpan detail, update stok, dan log pergerakan) ...
            conn.commit();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);


            clearForm();

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Transaksi Gagal: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }//GEN-LAST:event_btnBayarActionPerformed

    private void lblJudulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblJudulActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblJudulActionPerformed

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
        this.setVisible(false);
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
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BG_POSForm;
    private javax.swing.JButton btnBatalTransaksi;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnDashboard1;
    private javax.swing.JButton btnHapusItem;
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
    private javax.swing.JButton btnRiwayat;
    private javax.swing.JComboBox<String> cmbMetodeBayar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblDiskon;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JTextField lblJudul;
    private javax.swing.JLabel lblKembalian;
    private javax.swing.JLabel lblPeran;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTable tblShoppingCart;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtJumlahBayar;
    // End of variables declaration//GEN-END:variables
}
