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

/**
 * Kontroler dla głównej strony aplikacji.
 */
public class mainpageController {

    @FXML
    private Label label_pracownicy; // Etykieta dla sekcji pracowników

    @FXML
    private Label label_magazyn; // Etykieta dla sekcji magazynu

    @FXML
    private Label label_zadania; // Etykieta dla sekcji zadań

    @FXML
    private Label label_dostawcy; // Etykieta dla sekcji dostawców

    @FXML
    private Label label_informacje; // Etykieta dla sekcji informacji

    @FXML
    private Button logoutButton; // Przycisk wylogowania

    /**
     * Metoda inicjalizacyjna, wywoływana po załadowaniu FXML.
     */
    @FXML
    void initialize() {
        // Dodanie obsługi zdarzeń dla każdej etykiety
        label_pracownicy.setOnMouseClicked(event -> goToPage("pracownicy.fxml"));
        label_magazyn.setOnMouseClicked(event -> goToPage1("magazyn.fxml"));
        label_zadania.setOnMouseClicked(event -> goToPage2("zadania.fxml"));
        label_dostawcy.setOnMouseClicked(event -> goToPage("dostawcy.fxml"));
        label_informacje.setOnMouseClicked(event -> goToPage4("informacje.fxml"));
    }

    /**
     * Metoda obsługująca kliknięcie przycisku wylogowania.
     *
     * @param event zdarzenie kliknięcia przycisku
     */
    @FXML
    void logoutButtonClicked(ActionEvent event) {
        goToLoginPage(); // Przechodzi do strony logowania
    }

    /**
     * Przechodzi do strony logowania.
     */
    private void goToLoginPage() {
        try {
            // Ładowanie widoku logowania
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logowanie.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa wyjątków IO
        }
    }

    /**
     * Przechodzi do strony pracowników.
     *
     * @param pracownicy ścieżka do pliku FXML strony pracowników
     */
    private void goToPage(String pracownicy) {
        try {
            // Ładowanie widoku pracowników
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pracownicy.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_pracownicy.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa wyjątków IO
        }
    }

    /**
     * Przechodzi do strony magazynu.
     *
     * @param magazyn ścieżka do pliku FXML strony magazynu
     */
    private void goToPage1(String magazyn) {
        try {
            // Ładowanie widoku magazynu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("magazyn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_magazyn.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa wyjątków IO
        }
    }

    /**
     * Przechodzi do strony informacji.
     *
     * @param informcje ścieżka do pliku FXML strony informacji
     */
    private void goToPage4(String informcje) {
        try {
            // Ładowanie widoku informacji
            FXMLLoader loader = new FXMLLoader(getClass().getResource("informacje.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_informacje.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa wyjątków IO
        }
    }

    /**
     * Przechodzi do strony zadań.
     *
     * @param zadania ścieżka do pliku FXML strony zadań
     */
    private void goToPage2(String zadania) {
        try {
            // Ładowanie widoku zadań
            FXMLLoader loader = new FXMLLoader(getClass().getResource("zadania.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_zadania.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa wyjątków IO
        }
    }
}
