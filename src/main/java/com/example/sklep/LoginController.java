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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_password;

    @FXML
    private Button btn_login;

    @FXML
    private Button bt_rejestracja;

    @FXML
    void loginButtonClicked(ActionEvent event) {
        String username = tf_username.getText();
        String password = tf_password.getText();

        if (checkCredentials(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btn_login.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Wyświetl komunikat o błędnych danych logowania
            System.out.println("Błędny login lub hasło!");
        }
    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
        // Tutaj dodaj logikę rejestracji
        // Na przykład: przejdź do strony rejestracji
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rej.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) bt_rejestracja.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Zwróć true jeśli dane są poprawne, w przeciwnym razie false
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Jeśli wystąpił błąd, zwróć false
        }
    }

}
