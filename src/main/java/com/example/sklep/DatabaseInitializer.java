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

                        // Stworzenie tabeli pracownik
                        "CREATE TABLE IF NOT EXISTS pracownik (" +
                                "id int(11) NOT NULL AUTO_INCREMENT, " +
                                "imie varchar(50) DEFAULT NULL, " +
                                "nazwisko varchar(50) DEFAULT NULL, " +
                                "kontakt varchar(20) DEFAULT NULL, " +
                                "PRIMARY KEY (id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wprowadzenie danych do tabeli pracownik
                        "INSERT INTO pracownik (id, imie, nazwisko, kontakt) VALUES " +
                                "(1, 'Anna', 'Helon', 'ah@ur.pl'), " +
                                "(2, 'Maciej', 'Gawlak', 'mg@ur.pl'), " +
                                "(3, 'Kacper', 'Jarosz', 'kj@ur.pl'), " +
                                "(4, 'Jakub', 'Pacholarz', 'jp@ur.pl');",

                        // Stworzenie tabeli roles
                        "CREATE TABLE IF NOT EXISTS roles (" +
                                "role_id int(11) NOT NULL AUTO_INCREMENT, " +
                                "role_name varchar(50) DEFAULT NULL, " +
                                "PRIMARY KEY (role_id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wprowadzenie danych do tabeli roles
                        "INSERT INTO roles (role_id, role_name) VALUES " +
                                "(1, 'Pracownik'), " +
                                "(2, 'Admin'), " +
                                "(3, 'Kierownik');",

                        // Stworzenie tabeli taskpriority
                        "CREATE TABLE IF NOT EXISTS taskpriority (" +
                                "priority_id int(11) NOT NULL AUTO_INCREMENT, " +
                                "priority_name varchar(50) DEFAULT NULL, " +
                                "PRIMARY KEY (priority_id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wprowadzenie danych do taskpriority
                        "INSERT INTO taskpriority (priority_id, priority_name) VALUES " +
                                "(1, 'Niski'), " +
                                "(2, 'Średni'), " +
                                "(3, 'Wysoki');",

                        // Stworzenie tabeli tasks
                        "CREATE TABLE IF NOT EXISTS tasks (" +
                                "task_id int(11) NOT NULL AUTO_INCREMENT, " +
                                "task_name varchar(50) DEFAULT NULL, " +
                                "status_id int(11) DEFAULT NULL, " +
                                "PRIMARY KEY (task_id), " +
                                "KEY status_id (status_id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wprowadzenie danych do tabeli tasks
                        "INSERT INTO tasks (task_id, task_name, status_id) VALUES " +
                                "(1, 'Dostawa', 1), " +
                                "(2, 'Dotowarowanie', 1), " +
                                "(3, 'Sprzątanie', 1), " +
                                "(4, 'Prasowanie', 1);",

                        // Stworzenie tabeli taskstatus
                        "CREATE TABLE IF NOT EXISTS taskstatus (" +
                                "status_id int(11) NOT NULL AUTO_INCREMENT, " +
                                "status_name varchar(50) DEFAULT NULL, " +
                                "PRIMARY KEY (status_id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wstaw dane do taskstatus
                        "INSERT INTO taskstatus (status_id, status_name) VALUES " +
                                "(1, 'Rozpoczęcie'), " +
                                "(2, 'W toku'), " +
                                "(3, 'Zakończone');",

                        // Tworzenie tabeli użytkowników
                        "CREATE TABLE IF NOT EXISTS users (" +
                                "user_id int(11) NOT NULL AUTO_INCREMENT, " +
                                "username varchar(50) DEFAULT NULL, " +
                                "email varchar(100) NOT NULL, " +
                                "password varchar(255) NOT NULL, " +
                                "nr_tel varchar(20) DEFAULT NULL, " +
                                "role_id int(11) DEFAULT NULL, " +
                                "status_id int(11) DEFAULT NULL, " +
                                "priority_id int(11) DEFAULT NULL, " +
                                "PRIMARY KEY (user_id), " +
                                "KEY role_id (role_id), " +
                                "KEY status_id (status_id), " +
                                "KEY priority_id (priority_id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",

                        // Wstawianie danych do użytkowników
                        "INSERT INTO users (user_id, username, email, password, nr_tel, role_id, status_id, priority_id) VALUES " +
                                "(1, 'Maciej Gawlak', 'maciej@example.com', 'haslo123', '123-456-789', 1, 1, 1), " +
                                "(2, 'Jakub Pacholarz', 'jakub@example.com', 'haslo456', 123-456-789, 1, 2, 2), " +
                                "(3, 'Kacper Jarosz', 'kacper@example.com', 'haslo789', 123-456-789, 1, 3, 3), " +
                                "(4, 'Anna Helon', 'ah@ur.pl', '12345', 123-456-789, 3, 1, 3), " +
                                "(5, 'Wojciech Jaminski', 'wj@ur.pl', '12345', 123-456-789, 1, 2, 1), " +
                                "(6, 'Adam Nowak', 'an@ur.pl', '12345', 123-456-789, 1, 3, 2), " +
                                "(13, 'Jakub Siebida', 'jakub@ur.pl', '123456', 123-456-789, 1, 1, 1), " +
                                "(14, 'Jan Kowalski', 'jk@ur.pl', '54321', 123-456-789, 1, 1, 1), " +
                                "(15, 'Martyna Kowalska', 'mk@ur.pl', 'haslo321', 123-456-789, 1, 1, 1), " +
                                "(16, 'Dariusz Ped', 'dp@ur.pl', 'qwerty', 123-456-789, 1, 1, 1), " +
                                "(17, 'Ula Rakowska', 'ur@ur.pl', '12343', 123-456-789, 1, 1, 1), " +
                                "(18, 'Kacper Pe', 'kp@ur.pl', '1234', 123-456-789, 1, 1, 1), " +
                                "(19, 'Radoslaw Wydra', 'rw@ur.pl', '12345', 123-456-789, 1, 1, 1), " +
                                "(20, 'Martyna Jazwinska', 'mj@ue.wroc.pl', 'qwerty', 123-456-789, 1, 1, 1), " +
                                "(21, 'a', 'a', 'a', 123-456-789, 1, 1, 1);",


                        // Ograniczenia dla tabeli zadania
                        "ALTER TABLE tasks " +
                                "ADD CONSTRAINT tasks_ibfk_2 FOREIGN KEY (status_id) REFERENCES taskstatus (status_id);",

                        // Ograniczenia dla tabeli użytkowników
                        "ALTER TABLE users " +
                                "ADD CONSTRAINT users_ibfk_1 FOREIGN KEY (role_id) REFERENCES roles (role_id), " +
                                "ADD CONSTRAINT users_ibfk_2 FOREIGN KEY (status_id) REFERENCES taskstatus (status_id), " +
                                "ADD CONSTRAINT users_ibfk_3 FOREIGN KEY (priority_id) REFERENCES taskpriority (priority_id);"
                };

                // Wykonanie deklaracji SQL
                for (String sql : sqlStatements) {
                    statement.executeUpdate(sql);
                }

                System.out.println("Baza Danych i tabele zostały utworzone pomyślnie.");
            } else {
                System.out.println("Baza danych '" + DB_NAME + "' już istnieje.");
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
