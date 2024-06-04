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
    private Label label_informacje;

    @FXML
    private Button logoutButton;

    @FXML
    void initialize() {
        // Dodajemy obsługę zdarzenia dla każdej etykiety
        label_pracownicy.setOnMouseClicked(event -> goToPage("pracownicy.fxml"));
        label_magazyn.setOnMouseClicked(event -> goToPage1("magazyn.fxml"));
        label_zadania.setOnMouseClicked(event -> goToPage2("zadania.fxml"));
        label_dostawcy.setOnMouseClicked(event -> goToPage("dostawcy.fxml"));
        label_informacje.setOnMouseClicked(event -> goToPage4("informacje.fxml"));
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
            Stage stage = (Stage) label_pracownicy.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void goToPage1(String magazyn) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("magazyn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_magazyn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void goToPage4(String informcje) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("informacje.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_informacje.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void goToPage2(String zadania) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("zadania.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_zadania.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
