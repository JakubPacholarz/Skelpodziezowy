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

public class pracownicyController implements Initializable {

    @FXML
    private GridPane gridPane;

    @FXML
    private Label label_sklep;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String connectQuery = "SELECT imie, nazwisko, kontakt FROM pracownik";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            int row = 0;
            while (queryOutput.next()) {
                String imie = queryOutput.getString("imie");
                String nazwisko = queryOutput.getString("nazwisko");
                String kontakt = queryOutput.getString("kontakt");

                Label labelImie = new Label(imie);
                Label labelNazwisko = new Label(nazwisko);
                Label labelKontakt = new Label(kontakt);

                labelImie.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                labelNazwisko.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                labelKontakt.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                gridPane.addRow(row++, labelImie, labelNazwisko, labelKontakt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToMainPage(MouseEvent event) {
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
}
