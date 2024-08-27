package com.example.sklep;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "sklep";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            // Sprawdź czy baza danych już istnieje
            if (!databaseExists(statement, DB_NAME)) {
                // Utwórz bazę danych, jeśli nie istnieje
                String createDatabaseQuery = "CREATE DATABASE " + DB_NAME;
                statement.executeUpdate(createDatabaseQuery);

                // Używanie nowo utworzonej bazy danych
                String useDatabaseQuery = "USE " + DB_NAME;
                statement.executeUpdate(useDatabaseQuery);

                // Instrukcje SQL do tworzenia tabel i wstawiania danych
                String[] sqlStatements = {
                        // Stworzenie tabeli magazyn
                        "CREATE TABLE IF NOT EXISTS magazyn (" +
                                "dostawca varchar(255) DEFAULT NULL, " +
                                "zasoby int(11) DEFAULT NULL, " +
                                "kontakt varchar(255) DEFAULT NULL" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wprowadzenie danych do tabeli magazyn
                        "INSERT INTO magazyn (dostawca, zasoby, kontakt) VALUES " +
                                "('Firma A', 100, '123-456-789'), " +
                                "('Firma B', 100, '123-456-789'), " +
                                "('Firma C', 100, '123-456-789'), " +
                                "('Firma D', 100, '123-456-789');",

                        // Stworzenie tabeli tasks
                        "CREATE TABLE IF NOT EXISTS tasks (" +
                                "task_id int(11) NOT NULL AUTO_INCREMENT, " +
                                "task_name varchar(50) DEFAULT NULL, " +
                                "PRIMARY KEY (task_id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wprowadzenie danych do tabeli tasks
                        "INSERT INTO tasks (task_name) VALUES " +
                                "('Dostawa'), " +
                                "('Dotowarowanie'), " +
                                "('Sprzątanie'), " +
                                "('Prasowanie');",

                        // Stworzenie tabeli taskstatus
                        "CREATE TABLE IF NOT EXISTS taskstatus (" +
                                "status_id int(11) NOT NULL AUTO_INCREMENT, " +
                                "status_name varchar(50) DEFAULT NULL, " +
                                "PRIMARY KEY (status_id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wprowadzenie danych do taskstatus
                        "INSERT INTO taskstatus (status_id, status_name) VALUES " +
                                "(1, 'Gotowe'), " +
                                "(2, 'W toku'), " +
                                "(3, 'Zakończone');",

                        // Stworzenie tabeli użytkowników
                        "CREATE TABLE IF NOT EXISTS users (" +
                                "user_id int(11) NOT NULL AUTO_INCREMENT, " +
                                "imie varchar(50) DEFAULT NULL, " +
                                "nazwisko varchar(50) DEFAULT NULL, " +
                                "email varchar(100) NOT NULL, " +
                                "password varchar(255) NOT NULL, " +
                                "rola ENUM('pracownik', 'kierownik', 'admin') DEFAULT 'pracownik', " +
                                "total_time_spent INT DEFAULT 0, " +
                                "approved BOOLEAN DEFAULT false, " +  // Nowe pole approved
                                "PRIMARY KEY (user_id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wprowadzenie danych do użytkowników
                        "INSERT INTO users (imie, nazwisko, email, password, rola, approved) VALUES " +
                                "('Maciej', 'Gawlak', 'm', 'm', 'pracownik', false), " +
                                "('Jakub', 'Pacholarz', 'jakub@example.com', 'haslo456', 'pracownik', false), " +
                                "('Kacper', 'Jarosz', 'k', 'k', 'kierownik', false), " +
                                "('Anna', 'Helon', 'ah@ur.pl', '12345', 'kierownik', false), " +
                                "('a','a', 'a', 'a', 'admin', true);",

                        // Stworzenie tabeli user_tasks
                        "CREATE TABLE IF NOT EXISTS user_tasks (" +
                                "user_id int(11), " +
                                "task_id int(11), " +
                                "start_date DATETIME, " +
                                "end_date DATETIME, " +
                                "status_id int(11), " +  // Include status_id column from taskstatus
                                "PRIMARY KEY (user_id, task_id), " +
                                "FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE, " +
                                "FOREIGN KEY (task_id) REFERENCES tasks (task_id) ON DELETE CASCADE" +  // Remove FOREIGN KEY to status_id
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;"
                };

                // Wykonanie deklaracji SQL
                for (String sql : sqlStatements) {
                    statement.executeUpdate(sql);
                }

                System.out.println("Baza Danych i tabele zostały utworzone pomyślnie.");
            } else {
                System.out.println("Baza danych '" + DB_NAME + "' już istnieje.");

                // Używanie istniejącej bazy danych
                String useDatabaseQuery = "USE " + DB_NAME;
                statement.executeUpdate(useDatabaseQuery);

                // Usuń tabelę pracownik
                String dropPracownikTableQuery = "DROP TABLE IF EXISTS pracownik";
                statement.executeUpdate(dropPracownikTableQuery);

                // Dodaj kolumnę rola do tabeli users (jeśli jeszcze nie istnieje)
                String addRolaColumnQuery = "ALTER TABLE users ADD COLUMN rola ENUM('pracownik', 'kierownik', 'admin') DEFAULT 'pracownik'";
                try {
                    statement.executeUpdate(addRolaColumnQuery);
                } catch (SQLException e) {
                    if (e.getErrorCode() != 1060) { // Error code 1060 means duplicate column name
                        throw e;
                    }
                }

                // Dodaj kolumnę total_time_spent do tabeli users (jeśli jeszcze nie istnieje)
                String addTotalTimeSpentColumnQuery = "ALTER TABLE users ADD COLUMN total_time_spent INT DEFAULT 0";
                try {
                    statement.executeUpdate(addTotalTimeSpentColumnQuery);
                } catch (SQLException e) {
                    if (e.getErrorCode() != 1060) { // Error code 1060 means duplicate column name
                        throw e;
                    }
                }

                // Dodaj kolumnę approved do tabeli users (jeśli jeszcze nie istnieje)
                String addApprovedColumnQuery = "ALTER TABLE users ADD COLUMN approved BOOLEAN DEFAULT false";
                try {
                    statement.executeUpdate(addApprovedColumnQuery);
                } catch (SQLException e) {
                    if (e.getErrorCode() != 1060) { // Error code 1060 means duplicate column name
                        throw e;
                    }
                }

                // Ustawienie użytkownika 'admin' jako zatwierdzony
                String updateAdminApprovedQuery = "UPDATE users SET approved = true WHERE rola = 'admin'";
                statement.executeUpdate(updateAdminApprovedQuery);

                System.out.println("Tabela 'pracownik' została usunięta, a kolumny 'rola', 'total_time_spent' oraz 'approved' zostały dodane do tabeli 'users'.");

                // Dodanie kolumn start_date i end_date do tabeli user_tasks (jeśli jeszcze nie istnieją)
                String alterUserTasksTableQuery = "ALTER TABLE user_tasks " +
                        "ADD COLUMN start_date DATE, " +
                        "ADD COLUMN end_date DATE";
                try {
                    statement.executeUpdate(alterUserTasksTableQuery);
                } catch (SQLException ex) {
                    if (ex.getErrorCode() != 1060) { // Error code 1060 means duplicate column name
                        throw ex;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metoda pomocnicza do sprawdzania istnienia bazy danych
    private static boolean databaseExists(Statement statement, String dbName) throws SQLException {
        String checkDatabaseQuery = "SHOW DATABASES LIKE '" + dbName + "'";
        return statement.executeQuery(checkDatabaseQuery).next();
    }
}
