package com.example.sklep;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RaportController {

    DatabaseConnection connectNow = new DatabaseConnection();
    Connection connection = connectNow.getConnection();
    @FXML
    private Label raporty;
    @FXML
    private Label label_sklep;

    @FXML
    private ChoiceBox<String> reportTypeChoiceBox; // ChoiceBox dla wyboru typu raportu

    @FXML
    private ChoiceBox<String> filterTypeChoiceBox; // ChoiceBox dla wyboru typu filtru

    @FXML
    private ChoiceBox<String> filterPhraseChoiceBox; // ChoiceBox dla frazy filtrującej

    @FXML
    private Button generateReportButton; // Przycisk do generowania raportu

    @FXML
    private Label phraseLabel; // Etykieta dla frazy filtrującej

    private FileChooser fileChooser; // Obiekt do wyboru pliku dla zapisu raportu

    @FXML
    private void initialize() {
        // Inicjalizacja choiceboxa z typami raportów
        reportTypeChoiceBox.getItems().addAll("Magazyn", "Zadania", "Użytkownicy");

        // Inicjalizacja choiceboxa z typami filtrów na podstawie wyboru raportu
        reportTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Magazyn".equals(newValue)) {
                filterTypeChoiceBox.getItems().setAll("Wszystko", "Zasoby", "Dostawca");
                filterTypeChoiceBox.setValue("Wszystko");
                phraseLabel.setVisible(true);
                filterPhraseChoiceBox.setVisible(true);
            } else if ("Zadania".equals(newValue)) {
                filterTypeChoiceBox.getItems().setAll("Wszystko");
                filterTypeChoiceBox.setValue("Wszystko");
                phraseLabel.setVisible(false);
                filterPhraseChoiceBox.setVisible(false);
            } else if ("Użytkownicy".equals(newValue)) {
                filterTypeChoiceBox.getItems().setAll("Wszystko", "Imie", "Nazwisko", "Rola");
                filterTypeChoiceBox.setValue("Wszystko");
                phraseLabel.setVisible(true);
                filterPhraseChoiceBox.setVisible(true);
            }
        });

        // Inicjalizacja listenera dla wyboru typu filtru
        filterTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            populateFilterPhraseChoiceBox();
        });

        // Inicjalizacja FileChooser do wyboru miejsca zapisu raportu
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
    }

    @FXML
    private void handleGenerateReport() {
        String reportType = reportTypeChoiceBox.getValue();
        String filterType = filterTypeChoiceBox.getValue();
        String filterPhrase = filterPhraseChoiceBox.getValue(); // Inicjalizacja frazy filtra (może być pusta)

        // Sprawdzenie, czy wszystkie wymagane wartości są ustawione
        if (reportType == null || filterType == null) {
            System.out.println("Nie wybrano raportu lub filtru.");
            return;
        }

        // Wywołanie generowania raportu PDF z klasy PDF
        try {
            PDF pdf = new PDF();
            File file = fileChooser.showSaveDialog(null); // Wybór miejsca zapisu raportu PDF
            if (file != null) {
                Integer filterTypeInt = getFilterType(reportType, filterType);
                System.out.println("Wybrany Typ Raportu: " + reportType);
                System.out.println("Wybrany Typ Filtra: " + filterType);
                System.out.println("Fraza Filtrująca: " + filterPhrase);

                // Jeśli filterType == "Brak", ustaw frazę filtrującą na "Brak"
                if ("Brak".equals(filterType)) {
                    filterPhrase = "Brak";
                }

                if (filterTypeInt != null) {
                    System.out.println("Integer Typu Filtra: " + filterTypeInt);
                    pdf.manipulatePdf(file.getAbsolutePath(), reportType, filterTypeInt, filterPhrase);
                } else {
                    System.out.println("Nieobsługiwany typ filtru dla raportu: " + reportType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd podczas generowania raportu: " + e.getMessage());
        }
    }




    @FXML
    private void goToMainPage(MouseEvent event) {
        System.out.println("Label clicked!");  // Add this line for debugging
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_sklep.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metoda pomocnicza do uzyskania int wartości dla filtrów
    private Integer getFilterType(String reportType, String filterType) {
        if ("Magazyn".equals(reportType)) {
            switch (filterType) {
                case "Wszystko":
                    return 1;
                case "Zasoby":
                    return 2;
                case "Dostawca":
                    return 3;
                default:
                    return null;
            }
        } else if ("Zadania".equals(reportType)) {
            switch (filterType) {
                case "Wszystko":
                    return 1;
                default:
                    return null;
            }
        } else if ("Użytkownicy".equals(reportType)) {
            switch (filterType) {
                case "Wszystko":
                    return 1;
                case "Imie":
                    return 2;
                case "Nazwisko":
                    return 3;
                case "Rola":
                    return 4;
                default:
                    return null;
            }
        }
        return null;
    }



    private void populateFilterPhraseChoiceBox() {
        String filterType = filterTypeChoiceBox.getValue();
        filterPhraseChoiceBox.getItems().clear();

        String query = null;
        if(filterType!=null) {
            switch (filterType) {
                case "Imie":
                    query = "SELECT DISTINCT imie FROM users";
                    break;
                case "Nazwisko":
                    query = "SELECT DISTINCT nazwisko FROM users";
                    break;
                case "Rola":
                    query = "SELECT DISTINCT rola FROM users";
                    break;
                case "Zasoby":
                    query = "SELECT DISTINCT zasoby FROM magazyn";
                    break;
                case "Dostawca":
                    query = "SELECT DISTINCT dostawca FROM magazyn";
                    break;
            }

            if (query != null) {
                try (PreparedStatement stmt = connection.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        filterPhraseChoiceBox.getItems().add(rs.getString(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Wystąpił błąd podczas pobierania danych: " + e.getMessage());
                }
            }
        } else {

        }
    }
}
