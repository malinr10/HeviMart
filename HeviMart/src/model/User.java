/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author mdr
 */
public class User {
    private int idPengguna;
    private String namaPengguna; // Ini adalah kolom 'username' Anda di DB
    private String kataSandi;    // Ini adalah kolom 'password_hash' di DB
    private String namaLengkap;
    private String peran;
    private String email;
    private String telepon;
    
    public User() {}

    public User(String namaLengkap, String namaPengguna, String peran, String email, String telepon, boolean aktif) {
        this.namaLengkap = namaLengkap;
        this.namaPengguna = namaPengguna;
        this.peran = peran;
        this.email = email;
        this.telepon = telepon;
    }

    // Getters
    public int getIdPengguna() { return idPengguna; }
    public String getNamaPengguna() { return namaPengguna; } // Untuk username
    public String getKataSandi() { return kataSandi; } // Untuk password hash
    public String getNamaLengkap() { return namaLengkap; }
    public String getPeran() { return peran; }
    public String getEmail() { return email; }
    public String getTelepon() { return telepon; }
    // public LocalDateTime getDibuatPada() { return dibuatPada; }
    // public LocalDateTime getDiperbaruiPada() { return diperbaruiPada; }

    // Setters
    public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }
    public void setNamaPengguna(String namaPengguna) { this.namaPengguna = namaPengguna; } // Untuk username
    public void setKataSandi(String kataSandi) { this.kataSandi = kataSandi; } // Untuk password hash
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public void setPeran(String peran) { this.peran = peran; }
    public void setEmail(String email) { this.email = email; }
    public void setTelepon(String telepon) { this.telepon = telepon; }
    // public void setDibuatPada(LocalDateTime dibuatPada) { this.dibuatPada = dibuatPada; }
    // public void setDiperbaruiPada(LocalDateTime diperbaruiPada) { this.diperbaruiPada = diperbaruiPada; }

}
