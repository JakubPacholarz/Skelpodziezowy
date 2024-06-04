package com.example.sklep;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class mainpageController {

    @FXML
    private Label label_pracownicy;

    @FXML
    private Label label_magazyn;

    @FXML
    private Label label_zadania;

    @FXML
    private Label label_dostawcy;

    @FXML
    private Label label_dostawcy2;

    @FXML
    private Button logoutButton;

    @FXML
    void initialize() {
        // Dodajemy obsługę zdarzenia dla każdej etykiety
        label_pracownicy.setOnMouseClicked(event -> goToPage("pracownicy.fxml"));
        label_magazyn.setOnMouseClicked(event -> goToPage("magazyn.fxml"));
        label_zadania.setOnMouseClicked(event -> goToPage("zadania.fxml"));
        label_dostawcy.setOnMouseClicked(event -> goToPage("dostawcy.fxml"));
        label_dostawcy2.setOnMouseClicked(event -> goToPage("dostawcy.fxml"));
    }
    @FXML
    void logoutButtonClicked(ActionEvent event) {
        goToLoginPage();
    }

    private void goToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logowanie.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToPage(String pracownicy) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pracownicy.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
