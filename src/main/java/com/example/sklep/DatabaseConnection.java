package com.example.sklep;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public Connection getConnection() {
        Connection connection;
        try {
            String dbURL = "jdbc:mysql://localhost:3306/sklep";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connected to the database.");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
