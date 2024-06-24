package com.example.sklep;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Klasa odpowiedzialna za nawiązanie połączenia z bazą danych MySQL.
 */
public class DatabaseConnection {

    /**
     * Metoda uzyskująca połączenie z bazą danych MySQL.
     *
     * @return obiekt Connection reprezentujący połączenie z bazą danych lub null w przypadku błędu.
     */
    public Connection getConnection() {
        Connection connection; // Deklaracja zmiennej do przechowywania połączenia
        try {
            // URL do bazy danych, nazwa użytkownika i hasło
            String dbURL = "jdbc:mysql://localhost:3306/sklep";
            String username = "root";
            String password = "";

            // Nawiązanie połączenia z bazą danych
            connection = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connected to the database."); // Informacja o nawiązaniu połączenia

            return connection; // Zwrócenie obiektu Connection
        } catch (Exception e) {
            e.printStackTrace(); // Wyświetlenie informacji o błędzie
            return null; // Zwrócenie null w przypadku błędu
        }
    }
}
