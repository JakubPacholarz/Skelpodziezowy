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

public class RejestracjaController {

    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_password;

    @FXML
    private TextField tf_email; // Dodaj pole dla pola email

    @FXML
    private Button btn_rejestracja;

    @FXML
    private Button btn_login;

    @FXML
    void rejestracjaButtonClicked(ActionEvent event) {
        // Pobierz dane z formularza
        String username = tf_username.getText();
        String password = tf_password.getText();
        String email = tf_email.getText(); // Pobierz wartość pola email z formularza

        // Dodaj dane do bazy danych
        boolean registrationSuccessful = addRegistrationToDatabase(username, email, password);

        // Jeśli rejestracja zakończyła się powodzeniem, wróć do strony logowania
        if (registrationSuccessful) {
            goToLoginPage();
        }
    }

    @FXML
    void loginButtonClicked(ActionEvent event) {
        // Wróć do strony logowania
        goToLoginPage();
    }

    private boolean addRegistrationToDatabase(String username, String email, String password) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sklep", "root", "");
            PreparedStatement statement = conn.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.executeUpdate();
            conn.close();
            return true; // Zwróć true, jeśli rejestracja zakończyła się powodzeniem
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Zwróć false w przypadku błędu rejestracji
        }
    }

    private void goToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logowanie.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btn_login.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
