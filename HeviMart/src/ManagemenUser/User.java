/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ManagemenUser;

/**
 *
 * @author mdr
 */
public class User {
    private int idPengguna;
    private String namaLengkap;
    private String namaPengguna; // Ini akan menjadi username
    private String peran;
    private String email;
    private String telepon;
    private String kataSandi; // Hanya untuk internal DAO, jangan expose langsung di GUI
    private boolean aktif;
    
    public User(){
   
    }

    // Constructor untuk mengambil data dari DB
    public User(int idPengguna, String namaLengkap, String namaPengguna, String peran, String email, String telepon, String kataSandi, boolean aktif) {
        this.idPengguna = idPengguna;
        this.namaLengkap = namaLengkap;
        this.namaPengguna = namaPengguna;
        this.peran = peran;
        this.email = email;
        this.telepon = telepon;
        this.kataSandi = kataSandi; // Kata sandi yang sudah di-hash
        this.aktif = aktif;
    }
    
    // Constructor untuk tambah/edit data (tanpa ID atau kata sandi langsung)
    public User(String namaLengkap, String namaPengguna, String peran, String email, String telepon, boolean aktif) {
        this.namaLengkap = namaLengkap;
        this.namaPengguna = namaPengguna;
        this.peran = peran;
        this.email = email;
        this.telepon = telepon;
        this.aktif = aktif;
    }

    // Getters
    public int getIdPengguna() {
        return idPengguna;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getNamaPengguna() {
        return namaPengguna;
    }

    public String getPeran() {
        return peran;
    }

    public String getEmail() {
        return email;
    }

    public String getTelepon() {
        return telepon;
    }

    public String getKataSandi() {
        return kataSandi;
    }
    
    public boolean isAktif() {
        return aktif;
    }

    // Setters (jika diperlukan untuk update data di objek sebelum disimpan ke DB)
    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public void setNamaPengguna(String namaPengguna) {
        this.namaPengguna = namaPengguna;
    }

    public void setPeran(String peran) {
        this.peran = peran;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public void setKataSandi(String kataSandi) {
        this.kataSandi = kataSandi;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

}
