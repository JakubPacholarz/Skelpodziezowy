package com.example.sklep;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Kontroler odpowiedzialny za logowanie użytkownika.
 */
public class LoginController {

    @FXML
    private TextField tf_username; // Pole tekstowe dla nazwy użytkownika

    @FXML
    private TextField tf_password; // Pole tekstowe dla hasła

    @FXML
    private Button btn_login; // Przycisk logowania

    @FXML
    private Button bt_rejestracja; // Przycisk rejestracji

    @FXML
    private Label lbl_errorMessage; // Etykieta dla komunikatów o błędach

    /**
     * Metoda obsługująca zdarzenie kliknięcia przycisku logowania.
     *
     * @param event zdarzenie kliknięcia przycisku
     */
    @FXML
    void loginButtonClicked(ActionEvent event) {
        String username = tf_username.getText();
        String password = tf_password.getText(); // Hasło wprowadzone przez użytkownika

        if (checkCredentials(username, password)) { // Sprawdzanie poprawności danych logowania
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btn_login.getScene().getWindow();
                stage.setScene(new Scene(root)); // Ustawienie nowej sceny
            } catch (IOException e) {
                e.printStackTrace(); // Obsługa wyjątków IO
            }
        } else {
            lbl_errorMessage.setText("Błędny login lub hasło!"); // Ustawienie komunikatu o błędzie
        }
    }

    /**
     * Metoda obsługująca zdarzenie kliknięcia przycisku rejestracji.
     *
     * @param event zdarzenie kliknięcia przycisku
     */
    @FXML
    void registerButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rej.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) bt_rejestracja.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa wyjątków IO
        }
    }

    /**
     * Sprawdza poprawność danych logowania.
     *
     * @param username nazwa użytkownika
     * @param password hasło
     * @return true jeśli dane są poprawne, false w przeciwnym razie
     */
    private boolean checkCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Jeśli użytkownik istnieje, pobierz hasło z bazy danych
                String passwordFromDatabase = resultSet.getString("password");
                // Porównaj hasło wprowadzone przez użytkownika z hasłem z bazy danych
                return password.equals(passwordFromDatabase);
            } else {
                // Jeśli nie ma użytkownika o podanym adresie email, zwróć false
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Obsługa wyjątków SQL
            return false;
        }
    }
}
