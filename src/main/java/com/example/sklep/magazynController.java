package com.example.sklep;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Kontroler dla widoku magazynu.
 */
public class magazynController implements Initializable {

    @FXML
    private GridPane gridPane; // Siatka do wyświetlania danych magazynu

    @FXML
    private Label label_sklep; // Etykieta z nazwą sklepu

    /**
     * Metoda inicjalizacyjna, wywoływana po załadowaniu FXML.
     *
     * @param location lokalizacja zasobu
     * @param resources zasoby
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nawiązanie połączenia z bazą danych
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Zapytanie SQL do pobrania danych z tabeli magazyn
        String connectQuery = "SELECT dostawca, zasoby, kontakt FROM magazyn";

        try {
            // Wykonanie zapytania SQL
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            // Dodanie danych do siatki
            int row = 0;
            while (queryOutput.next()) {
                String dostawca = queryOutput.getString("dostawca");
                String zasoby = queryOutput.getString("zasoby");
                String kontakt = queryOutput.getString("kontakt");

                // Tworzenie etykiet z danymi
                Label labeldostawca = new Label(dostawca);
                Label labelZasoby = new Label(zasoby);
                Label labelKontakt = new Label(kontakt);

                // Ustawianie stylów dla etykiet
                labeldostawca.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                labelZasoby.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                labelKontakt.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                // Dodanie wiersza do siatki
                gridPane.addRow(row++, labeldostawca, labelZasoby, labelKontakt);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Obsługa wyjątków
        }
    }

    /**
     * Przechodzi do głównej strony aplikacji po kliknięciu na etykietę.
     *
     * @param event zdarzenie kliknięcia myszą
     */
    @FXML
    private void goToMainPage(MouseEvent event) {
        try {
            // Ładowanie widoku głównej strony
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_sklep.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
            stage.show(); // Wyświetlenie sceny
        } catch (Exception e) {
            e.printStackTrace(); // Obsługa wyjątków
        }
    }
}
