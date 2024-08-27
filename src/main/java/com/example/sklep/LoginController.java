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
    private TextField tf_email;

    @FXML
    private TextField tf_password;

    @FXML
    private Button btn_login;

    @FXML
    private Button bt_rejestracja;

    @FXML
    private Label lbl_errorMessage;


    // Metoda obsługująca logowanie
    public void loginButtonClicked(ActionEvent event) {
        String email = tf_email.getText();
        String password = tf_password.getText();

        int userId = checkCredentials(email, password);
        if (userId != -1) {
            // Sprawdzenie czy użytkownik jest zatwierdzony przez admina
            boolean approved = isUserApproved(userId);
            if (approved) {
                // Ustawienie ID użytkownika w sesji
                UserSession.getInstance().setLoggedInUserId(userId);

                // Pobranie roli użytkownika z bazy danych
                String userRole = getUserRole(userId);
                UserSession.getInstance().setUserRole(userRole);

                // Przełączenie na główną stronę po zalogowaniu
                switchToMainPage(userRole);
            } else {
                lbl_errorMessage.setText("Twoje konto czeka na zatwierdzenie przez administratora.");
            }
        } else {
            lbl_errorMessage.setText("Błędny login lub hasło!");
        }
    }

    // Metoda sprawdzająca czy użytkownik jest zatwierdzony przez administratora
    private boolean isUserApproved(int userId) {
        String query = "SELECT approved FROM users WHERE user_id = ?";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("approved");
            } else {
                return false; // Nie znaleziono użytkownika o podanym ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metoda sprawdzająca poprawność danych logowania
    private int checkCredentials(String email, String password) {
        String query = "SELECT user_id FROM users WHERE email = ? AND password = ?";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id"); // Zwraca ID użytkownika, jeśli dane są poprawne
            } else {
                return -1; // Dane logowania są niepoprawne
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Metoda pobierająca rolę użytkownika na podstawie ID
    private String getUserRole(int userId) {
        String query = "SELECT rola FROM users WHERE user_id = ?";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("rola");
            } else {
                return null; // Nie znaleziono użytkownika o podanym ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Obsługa kliknięcia przycisku rejestracji
    @FXML
    void registerButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rej.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) bt_rejestracja.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda przełączająca na główną stronę aplikacji w zależności od roli użytkownika
    private void switchToMainPage(String userRole) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = loader.load();

            // Pobranie kontrolera strony głównej
            mainpageController controller = loader.getController();
            controller.setUserRole(userRole); // Przekazanie roli użytkownika do kontrolera

            // Ustawienie sceny
            Stage stage = (Stage) btn_login.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
