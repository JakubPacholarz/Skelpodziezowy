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

public class magazynController implements Initializable {

    @FXML
    private GridPane gridPane;

    @FXML
    private Label label_sklep;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String connectQuery = "SELECT dostawca, zasoby, kontakt FROM magazyn";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            int row = 0;
            while (queryOutput.next()) {
                String dostawca = queryOutput.getString("dostawca");
                String zasoby = queryOutput.getString("zasoby");
                String kontakt = queryOutput.getString("kontakt");

                Label labeldostawca = new Label(dostawca);
                Label labelZasoby = new Label(zasoby);
                Label labelKontakt = new Label(kontakt);

                labeldostawca.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                labelZasoby.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                labelKontakt.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                gridPane.addRow(row++, labeldostawca, labelZasoby, labelKontakt);
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