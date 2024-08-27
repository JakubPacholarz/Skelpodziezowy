package com.example.sklep;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Kontroler obsługujący proces rejestracji użytkownika.
 */
public class RejestracjaController {

    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_nazwisko; // Pole tekstowe dla nazwiska użytkownika

    @FXML
    private TextField tf_password; // Pole tekstowe dla hasła

    @FXML
    private TextField tf_email; // Pole tekstowe dla emaila

    @FXML
    private ComboBox<String> cb_role; // ComboBox dla roli użytkownika

    @FXML
    private Button btn_rejestracja; // Przycisk rejestracji

    @FXML
    private Button btn_login; // Przycisk logowania

    @FXML
    private Label lbl_errorMessage; // Etykieta do wyświetlania komunikatów o błędach

    /**
     * Inicjalizacja ComboBox z rolami.
     */
    @FXML
    public void initialize() {
        ObservableList<String> roles = FXCollections.observableArrayList("pracownik", "kierownik", "admin");
        cb_role.setItems(roles);
    }

    /**
     * Metoda obsługująca kliknięcie przycisku rejestracji.
     *
     * @param event zdarzenie kliknięcia przycisku
     */
    @FXML
    void rejestracjaButtonClicked(ActionEvent event) {
        String imie = tf_username.getText();
        String nazwisko = tf_nazwisko.getText();
        String password = tf_password.getText();
        String email = tf_email.getText();
        String role = cb_role.getValue();

        // Sprawdzenie czy wszystkie pola są wypełnione
        if (imie.isEmpty() || nazwisko.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            lbl_errorMessage.setText("Wszystkie pola są wymagane!");
            return;
        }

        // Sprawdzenie czy email jest już w bazie danych
        if (isEmailAlreadyRegistered(email)) {
            lbl_errorMessage.setText("Podany adres e-mail jest już zarejestrowany!");
            return;
        }

        // Dodanie rejestracji do bazy danych
        boolean registrationSuccessful = addRegistrationToDatabase(imie, nazwisko, email, password, role);

        // Przejście do strony logowania po pomyślnej rejestracji
        if (registrationSuccessful) {
            goToLoginPage();
        } else {
            lbl_errorMessage.setText("Rejestracja nie powiodła się. Spróbuj ponownie.");
        }
    }

    /**
     * Sprawdza czy podany adres e-mail jest już zarejestrowany w bazie danych.
     *
     * @param email adres e-mail do sprawdzenia
     * @return true jeśli email jest już zarejestrowany, false w przeciwnym razie
     */
    private boolean isEmailAlreadyRegistered(String email) {
        try {
            // Ustanowienie połączenia z bazą danych
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sklep", "root", "root");

            // Przygotowanie zapytania SQL
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            statement.setString(1, email);

            // Wykonanie zapytania
            ResultSet resultSet = statement.executeQuery();

            // Jeśli wynik zapytania zawiera jakieś wiersze, oznacza to że email jest już zarejestrowany
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Załóżmy że wystąpił błąd - dla bezpieczeństwa zwracamy true
        }
    }

    /**
     * Dodaje nową rejestrację do bazy danych.
     *
     * @param imie nazwa użytkownika
     * @param email email użytkownika
     * @param password hasło użytkownika
     * @param role rola użytkownika
     * @return true jeśli rejestracja powiodła się, false w przeciwnym razie
     */
    private boolean addRegistrationToDatabase(String imie, String nazwisko, String email, String password, String role) {
        try {
            // Ustanowienie połączenia z bazą danych
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sklep", "root", "root");

            // Przygotowanie zapytania SQL
            PreparedStatement statement = conn.prepareStatement("INSERT INTO users (imie, nazwisko, email, password, rola, approved) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, imie);
            statement.setString(2, nazwisko);
            statement.setString(3, email);
            statement.setString(4, password);
            statement.setString(5, role);
            statement.setBoolean(6, false); // Nowy użytkownik jest niezatwierdzony

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
     * Metoda obsługująca kliknięcie przycisku logowania.
     *
     * @param event zdarzenie kliknięcia przycisku
     */
    @FXML
    void loginButtonClicked(ActionEvent event) {
        goToLoginPage();
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
