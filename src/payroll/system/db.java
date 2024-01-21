/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package payroll.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class db {
    // Connection conn = null;
    public static Connection java_db() {

        try {
            Class.forName("org.sqlite.JDBC");
            // Fix the path and use forward slashes
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/C:/Users/Acer/Documents/NetBeansProjects/Payroll System/mydatabase.db");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
}