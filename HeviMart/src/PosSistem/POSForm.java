package PosSistem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
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

    /**
     * Creates new form POSForm
     */
    public POSForm(int idKasir) {
        this.ID_KASIR = idKasir;
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("SuperMart POS - Kasir ID: " + idKasir);

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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblShoppingCart = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblSubtotal = new javax.swing.JLabel();
        lblDiskon = new javax.swing.JLabel();
        lblGrandTotal = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        cmbMetodeBayar = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtJumlahBayar = new javax.swing.JTextField();
        lblKembalian = new javax.swing.JLabel();
        btnBayar = new javax.swing.JButton();
        btnHapusItem = new javax.swing.JButton();
        btnBatalTransaksi = new javax.swing.JButton();
        btnRiwayat = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout(0, 1));

        jLabel1.setText("Input Barcode");

        txtBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBarcodeActionPerformed(evt);
            }
        });

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

        jPanel3.setLayout(new java.awt.GridLayout(10, 1));

        lblSubtotal.setText("Subtotal");
        jPanel3.add(lblSubtotal);

        lblDiskon.setText("Diskon");
        jPanel3.add(lblDiskon);

        lblGrandTotal.setText("Grand Total :");
        jPanel3.add(lblGrandTotal);
        jPanel3.add(jSeparator1);

        jLabel3.setText("Metode Pembayaran");
        jPanel3.add(jLabel3);

        cmbMetodeBayar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "QRIS", "Kartu Debit", "Kartu Kredit" }));
        jPanel3.add(cmbMetodeBayar);

        jLabel2.setText("Jumlah Bayar : ");
        jPanel3.add(jLabel2);
        jPanel3.add(txtJumlahBayar);

        lblKembalian.setText("Kembalian :");
        jPanel3.add(lblKembalian);

        btnBayar.setText("Bayar");
        btnBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBayarActionPerformed(evt);
            }
        });
        jPanel3.add(btnBayar);

        btnHapusItem.setText("Hapus Item");
        btnHapusItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusItemActionPerformed(evt);
            }
        });

        btnBatalTransaksi.setText("Batal Transaksi");
        btnBatalTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalTransaksiActionPerformed(evt);
            }
        });

        btnRiwayat.setText("Riwayat");
        btnRiwayat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatActionPerformed(evt);
            }
        });

        btnCari.setText("Cari Produk");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnHapusItem, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRiwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(118, 118, 118)
                                .addComponent(btnBatalTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCari)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBatalTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRiwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapusItem, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(61, 61, 61))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBarcodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBarcodeActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        // TODO add your handling code here:
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

        // --- PROSES TRANSAKSI ---
        Connection conn = null;
        try {
            conn = koneksi.getKoneksi(); // Sesuaikan dengan nama kelas koneksi Anda
            conn.setAutoCommit(false);

            // Ambil semua data yang akan disimpan
            String metodeBayar = (String) cmbMetodeBayar.getSelectedItem();
            BigDecimal kembalian = jumlahBayar.subtract(this.grandTotal);
            BigDecimal totalDiskon = new BigDecimal(lblDiskon.getText().replaceAll("[^\\d,]", "").replace(",", "."));
            BigDecimal totalSebelumDiskon = this.grandTotal.add(totalDiskon);

            // Query INSERT yang sudah lengkap
            String sqlTrx = "INSERT INTO TRANSAKSI (kode_transaksi, id_kasir, total_harga, diskon, harga_akhir, metode_bayar, jumlah_bayar, kembalian) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtTrx = conn.prepareStatement(sqlTrx, Statement.RETURN_GENERATED_KEYS);

            String kodeTrx = "TRX-" + System.currentTimeMillis();
            pstmtTrx.setString(1, kodeTrx);
            pstmtTrx.setInt(2, this.ID_KASIR);
            pstmtTrx.setBigDecimal(3, totalSebelumDiskon); // total harga asli
            pstmtTrx.setBigDecimal(4, totalDiskon);         // total diskon
            pstmtTrx.setBigDecimal(5, this.grandTotal);      // harga akhir setelah diskon
            pstmtTrx.setString(6, metodeBayar);
            pstmtTrx.setBigDecimal(7, jumlahBayar);
            pstmtTrx.setBigDecimal(8, kembalian);
            pstmtTrx.executeUpdate();

            ResultSet rsKeys = pstmtTrx.getGeneratedKeys();
            int trxId = rsKeys.next() ? rsKeys.getInt(1) : 0;
            if (trxId == 0) {
                throw new SQLException("Gagal membuat transaksi utama.");
            }

            // Loop untuk menyimpan detail, update stok, dan log pergerakan
            // ... (sisipkan loop for dari jawaban sebelumnya di sini, isinya sudah benar)
            // ...
            conn.commit();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Panggil cetak struk (jika sudah dibuat)
            // cetakStruk(trxId, this.grandTotal, jumlahBayar, kembalian);
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatalTransaksi;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnRiwayat;
    private javax.swing.JComboBox<String> cmbMetodeBayar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblDiskon;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JLabel lblKembalian;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JTable tblShoppingCart;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtJumlahBayar;
    // End of variables declaration//GEN-END:variables
}
