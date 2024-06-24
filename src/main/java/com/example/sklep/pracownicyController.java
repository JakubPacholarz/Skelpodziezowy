package com.example.sklep;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Kontroler dla widoku pracowników.
 */
public class pracownicyController implements Initializable {

    @FXML
    private GridPane gridPane; // Siatka do wyświetlania danych pracowników

    @FXML
    private Label label_sklep; // Etykieta sklepu, używana do nawigacji

    /**
     * Metoda inicjalizacyjna, wywoływana po załadowaniu FXML.
     *
     * @param location  lokalizacja pliku FXML
     * @param resources zasoby do wykorzystania w kontrolerze
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tworzenie połączenia z bazą danych
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Zapytanie do bazy danych o pracowników
        String connectQuery = "SELECT imie, nazwisko, kontakt FROM pracownik";

        try {
            // Wykonanie zapytania
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            int row = 0; // Zmienna do śledzenia wierszy w GridPane
            while (queryOutput.next()) {
                // Pobranie danych pracowników
                String imie = queryOutput.getString("imie");
                String nazwisko = queryOutput.getString("nazwisko");
                String kontakt = queryOutput.getString("kontakt");

                // Tworzenie etykiet dla danych pracowników
                Label labelImie = new Label(imie);
                Label labelNazwisko = new Label(nazwisko);
                Label labelKontakt = new Label(kontakt);

                // Ustawienie stylu etykiet
                labelImie.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                labelNazwisko.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                labelKontakt.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                // Dodanie etykiet do GridPane w nowym wierszu
                gridPane.addRow(row++, labelImie, labelNazwisko, labelKontakt);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Obsługa wyjątków
        }
    }

    /**
     * Metoda obsługująca kliknięcie etykiety sklepu, przechodząca do głównej strony.
     *
     * @param event zdarzenie kliknięcia myszką
     */
    @FXML
    private void goToMainPage(MouseEvent event) {
        try {
            // Ładowanie widoku głównej strony
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_sklep.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
            stage.show(); // Wyświetlenie nowej sceny
        } catch (Exception e) {
            e.printStackTrace(); // Obsługa wyjątków
        }
    }
}
