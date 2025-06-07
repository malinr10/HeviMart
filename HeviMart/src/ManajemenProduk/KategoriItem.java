/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ManajemenProduk;

/**
 *
 * @author Lenovo
 */
public class KategoriItem {
    private int id;
    private String nama;

    public KategoriItem(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    // Ini adalah bagian terpenting!
    // JComboBox akan memanggil method ini untuk menampilkan teks ke pengguna.
    @Override
    public String toString() {
        return nama;
    }
}
