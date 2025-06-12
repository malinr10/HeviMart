/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author Lenovo
 */
public class UserSession {
    private static UserSession instance;

    // 2. Variabel untuk menyimpan data pengguna yang login.
    private int idPengguna;
    private String namaPengguna;
    private String namaLengkap;
    private String email;
    private String telepon;
    private String peran;

    // 3. Constructor dibuat 'private' agar tidak ada yang bisa membuat instance
    //    baru dari luar kelas ini (contoh: new UserSession() akan error).
    private UserSession() {
        // Private constructor.
    }

    /**
     * Metode statis untuk mendapatkan satu-satunya instance dari UserSession.
     * Ini adalah gerbang utama untuk mengakses sesi.
     * @return Instance tunggal dari UserSession.
     */
    public static UserSession getInstance() {
        // Jika instance belum pernah dibuat, buat sekarang.
        if (instance == null) {
            instance = new UserSession();
        }
        // Kembalikan instance yang ada.
        return instance;
    }

    /**
     * Metode untuk membuat atau mengisi data sesi setelah login berhasil.
     */
    public void createSession(int id, String username, String nama, String role, String email, String telepon) {
        this.idPengguna = id;
        this.namaPengguna = username;
        this.namaLengkap = nama;
        this.peran = role;
        this.email = email;
        this.telepon = telepon;
    }

    /**
     * Metode untuk menghancurkan data sesi saat logout.
     */
    public void cleanUserSession() {
        this.idPengguna = 0;
        this.namaPengguna = null;
        this.namaLengkap = null;
        this.peran = null;
        // Kita juga bisa menghancurkan instance-nya, tapi membersihkan data sudah cukup.
        // instance = null;
    }

    // --- GETTER METHODS ---
    // Metode untuk mengambil data dari sesi di form mana pun.

    public int getIdPengguna() {
        return idPengguna;
    }

    public String getNamaPengguna() {
        return namaPengguna;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getPeran() {
        return peran;
    }
    
    public String getEmail() { return email; }
    public String getTelepon() { return telepon; }
}
