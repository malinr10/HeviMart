package ManajemenInventori;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Lenovo
 */
public class SupplierItem {
    private int id;
    private String nama;

    public SupplierItem(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    // Method ini akan dipanggil JComboBox untuk menampilkan teks
    @Override
    public String toString() {
        return nama;
    }
}
