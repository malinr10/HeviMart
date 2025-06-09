package PosSistem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
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

        BigDecimal grandTotal = subtotalKotor.subtract(totalDiskon);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        // Pastikan Anda punya JLabel lblSubtotal, lblDiskon, dan lblGrandTotal di form Anda
        lblSubtotal.setText(formatRupiah.format(subtotalKotor));
        lblDiskon.setText(formatRupiah.format(totalDiskon));
        lblGrandTotal.setText(formatRupiah.format(grandTotal));

        updateKembalian();
    }

    private void updateKembalian() {
        try {
            BigDecimal grandTotal = new BigDecimal(lblGrandTotal.getText().replaceAll("[^\\d]", ""));
            BigDecimal jumlahBayar = new BigDecimal(txtJumlahBayar.getText());
            BigDecimal kembalian = jumlahBayar.subtract(grandTotal);

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
        txtJumlahBayar = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        lblKembalian = new javax.swing.JLabel();
        btnBayar = new javax.swing.JButton();
        btnHapusItem = new javax.swing.JButton();
        btnBatalTransaksi = new javax.swing.JButton();
        btnRiwayat = new javax.swing.JButton();

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

        jPanel3.setLayout(new java.awt.GridLayout(7, 1));

        lblSubtotal.setText("Subtotal");
        jPanel3.add(lblSubtotal);

        lblDiskon.setText("Diskon");
        jPanel3.add(lblDiskon);

        lblGrandTotal.setText("Grand Total :");
        jPanel3.add(lblGrandTotal);
        jPanel3.add(txtJumlahBayar);
        jPanel3.add(jSeparator1);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnHapusItem, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnRiwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBatalTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHapusItem, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatalTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRiwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        // TODO: Tambahkan validasi untuk memastikan jumlah bayar cukup

        // --- PERBAIKAN 1: Hitung semua total langsung dari tabel ---
        BigDecimal subtotalKotor = BigDecimal.ZERO;
        BigDecimal totalDiskon = BigDecimal.ZERO;
        BigDecimal grandTotal; // Ini akan menjadi harga_akhir

        for (int i = 0; i < modelTabel.getRowCount(); i++) {
            BigDecimal harga = (BigDecimal) modelTabel.getValueAt(i, 2);
            int jumlah = (int) modelTabel.getValueAt(i, 3);
            BigDecimal diskon = (BigDecimal) modelTabel.getValueAt(i, 4);

            subtotalKotor = subtotalKotor.add(harga.multiply(new BigDecimal(jumlah)));
            totalDiskon = totalDiskon.add(diskon);
        }
        grandTotal = subtotalKotor.subtract(totalDiskon);

        Connection conn = null;
        try {
            conn = koneksi.getKoneksi();
            conn.setAutoCommit(false); // === MULAI TRANSAKSI ===

            // --- PERBAIKAN 2: Gunakan query INSERT yang benar ---
            String sqlTrx = "INSERT INTO TRANSAKSI (kode_transaksi, id_kasir, total_harga, diskon, harga_akhir) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmtTrx = conn.prepareStatement(sqlTrx, Statement.RETURN_GENERATED_KEYS);
            String kodeTrx = "TRX-" + System.currentTimeMillis();

            pstmtTrx.setString(1, kodeTrx);
            pstmtTrx.setInt(2, this.ID_KASIR);
            pstmtTrx.setBigDecimal(3, subtotalKotor); // total_harga = harga sebelum diskon
            pstmtTrx.setBigDecimal(4, totalDiskon);   // diskon = total semua diskon
            pstmtTrx.setBigDecimal(5, grandTotal);    // harga_akhir = harga setelah diskon

            pstmtTrx.executeUpdate();

            ResultSet rsKeys = pstmtTrx.getGeneratedKeys();
            int trxId = rsKeys.next() ? rsKeys.getInt(1) : 0;
            if (trxId == 0) {
                throw new SQLException("Gagal membuat transaksi utama.");
            }

            // Siapkan PreparedStatement di luar loop untuk efisiensi
            String sqlDetail = "INSERT INTO DETAIL_TRANSAKSI (id_transaksi, id_produk, jumlah, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?)";
            String sqlStok = "UPDATE PRODUK SET jumlah_stok = jumlah_stok - ? WHERE id_produk = ?";
            String sqlLog = "INSERT INTO PERGERAKAN_STOK (id_produk, tipe, jumlah, keterangan) VALUES (?, 'keluar', ?, ?)";

            try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail); PreparedStatement pstmtStok = conn.prepareStatement(sqlStok); PreparedStatement pstmtLog = conn.prepareStatement(sqlLog)) {

                for (int i = 0; i < modelTabel.getRowCount(); i++) {
                    // --- PERBAIKAN 3: Gunakan cara yang aman untuk mengambil nilai ---
                    Object idProdukObj = modelTabel.getValueAt(i, 0);
                    Object hargaObj = modelTabel.getValueAt(i, 2);
                    Object jumlahObj = modelTabel.getValueAt(i, 3);
                    Object subtotalObj = modelTabel.getValueAt(i, 5); // Ambil dari kolom subtotal (index 5)

                    if (idProdukObj == null || jumlahObj == null || hargaObj == null || subtotalObj == null) {
                        throw new SQLException("Data di keranjang tidak valid pada baris " + (i + 1));
                    }

                    int idProduk = (Integer) idProdukObj;
                    int jumlah = (Integer) jumlahObj;
                    BigDecimal harga = (BigDecimal) hargaObj;
                    BigDecimal subtotal = (BigDecimal) subtotalObj;

                    // 2. INSERT ke DETAIL_TRANSAKSI
                    pstmtDetail.setInt(1, trxId);
                    pstmtDetail.setInt(2, idProduk);
                    pstmtDetail.setInt(3, jumlah);
                    pstmtDetail.setBigDecimal(4, harga);
                    pstmtDetail.setBigDecimal(5, subtotal);
                    pstmtDetail.addBatch();

                    // 3. UPDATE stok di tabel PRODUK
                    pstmtStok.setInt(1, jumlah);
                    pstmtStok.setInt(2, idProduk);
                    pstmtStok.addBatch();

                    // 4. INSERT ke PERGERAKAN_STOK
                    pstmtLog.setInt(1, idProduk);
                    pstmtLog.setInt(2, -jumlah); // Jumlah negatif karena keluar
                    pstmtLog.setString(3, "Penjualan via POS, Transaksi: " + kodeTrx);
                    pstmtLog.addBatch();
                }

                // Eksekusi semua perintah batch sekaligus
                pstmtDetail.executeBatch();
                pstmtStok.executeBatch();
                pstmtLog.executeBatch();
            }

            conn.commit(); // === SELESAIKAN TRANSAKSI ===
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Panggil fungsi cetak struk di sini jika ada
            // cetakStruk(trxId, ...);
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatalTransaksi;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnRiwayat;
    private javax.swing.JLabel jLabel1;
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
