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
    private Label lbl_errorMessage;

    @FXML
    void loginButtonClicked(ActionEvent event) {
        String username = tf_username.getText();
        String password = tf_password.getText(); // Haszowanie hasła

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
            lbl_errorMessage.setText("Błędny login lub hasło!");
        }
    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
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
            e.printStackTrace();
            return false;
        }
    }
}
