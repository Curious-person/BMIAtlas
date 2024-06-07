package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    
    public static Connection DBConnect() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish the database connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/calculatebmi", "root", "");
            System.out.println("Database Connected");
            return con;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database: " + e.getMessage());
            return null;
        }
    }
}
