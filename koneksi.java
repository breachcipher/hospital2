/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Advan
 */
public class koneksi {
    private Connection koneksi;
    public Connection Connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("berhasil konek");
        } catch (ClassNotFoundException ex) {
            System.out.println("gagal koneksi+ex");
        }
        String url = "jdbc:mysql://localhost:3306/rumahsakit";
        try{
            koneksi =DriverManager.getConnection(url,"root","");
            System.out.println("berhasil konek database");
        }
        catch(SQLException ex){
            System.out.println("gagal koneksi database");
        }
        return koneksi;
    }
}
