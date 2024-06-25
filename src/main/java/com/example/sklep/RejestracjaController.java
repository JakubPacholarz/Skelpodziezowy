package com.example.sklep;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.Label;

/**
 * Kontroler obsługujący proces rejestracji użytkownika.
 */
public class RejestracjaController {

    @FXML
    private TextField tf_username; // Pole tekstowe dla nazwy użytkownika

    @FXML
    private TextField tf_password; // Pole tekstowe dla hasła

    @FXML
    private TextField tf_email; // Pole tekstowe dla emaila

    @FXML
    private Button btn_rejestracja; // Przycisk rejestracji

    @FXML
    private Button btn_login; // Przycisk logowania

    @FXML
    private Label lbl_errorMessage; // Etykieta do wyświetlania komunikatów o błędach

    /**
     * Metoda obsługująca kliknięcie przycisku rejestracji.
     *
     * @param event zdarzenie kliknięcia przycisku
     */
    @FXML
    void rejestracjaButtonClicked(ActionEvent event) {
        String username = tf_username.getText();
        String password = tf_password.getText();
        String email = tf_email.getText();

        // Sprawdzenie czy wszystkie pola są wypełnione
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            lbl_errorMessage.setText("Wszystkie pola są wymagane!");
            return;
        }

        // Dodanie rejestracji do bazy danych
        boolean registrationSuccessful = addRegistrationToDatabase(username, email, password);

        // Przejście do strony logowania po pomyślnej rejestracji
        if (registrationSuccessful) {
            goToLoginPage();
        }
    }

    /**
     * Metoda obsługująca kliknięcie przycisku logowania.
     *
     * @param event zdarzenie kliknięcia przycisku
     */
    @FXML
    void loginButtonClicked(ActionEvent event) {
        goToLoginPage();
    }

    /**
     * Dodaje nową rejestrację do bazy danych.
     *
     * @param username nazwa użytkownika
     * @param email    email użytkownika
     * @param password hasło użytkownika
     * @return true jeśli rejestracja powiodła się, false w przeciwnym razie
     */
    private boolean addRegistrationToDatabase(String username, String email, String password) {
        try {
            // Ustanowienie połączenia z bazą danych
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sklep", "root", "root");
            // Przygotowanie zapytania SQL
            PreparedStatement statement = conn.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            // Wykonanie zapytania
            statement.executeUpdate();
            conn.close(); // Zamknięcie połączenia
            return true; // Rejestracja powiodła się
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Rejestracja nie powiodła się
        }
    }

    /**
     * Przechodzi do strony logowania.
     */
    private void goToLoginPage() {
        try {
            // Załadowanie widoku logowania
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logowanie.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btn_login.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa wyjątków
        }
    }
}
