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


public class RejestracjaController {

    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_password;

    @FXML
    private TextField tf_email;

    @FXML
    private Button btn_rejestracja;

    @FXML
    private Button btn_login;

    @FXML
    private Label lbl_errorMessage;

    @FXML
    void rejestracjaButtonClicked(ActionEvent event) {
        String username = tf_username.getText();
        String password = tf_password.getText();
        String email = tf_email.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            lbl_errorMessage.setText("Wszystkie pola są wymagane!");
            return;
        }

        boolean registrationSuccessful = addRegistrationToDatabase(username, email, password);

        if (registrationSuccessful) {
            goToLoginPage();
        }
    }

    @FXML
    void loginButtonClicked(ActionEvent event) {
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
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
