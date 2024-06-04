package com.example.sklep;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class zadaniaController implements Initializable {

    @FXML
    private GridPane gridPane;

    @FXML
    private Label label_sklep;

    @FXML
    private ChoiceBox<String> pracownik;

    @FXML
    private TableView<Employee> tableView;

    @FXML
    private TableColumn<Employee, String> columnImie;

    @FXML
    private TableColumn<Employee, String> columnNazwisko;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String connectQuery = "SELECT imie, nazwisko FROM pracownik";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            while (queryOutput.next()) {
                String imie = queryOutput.getString("imie");
                String nazwisko = queryOutput.getString("nazwisko");
                pracownik.getItems().add(imie + " " + nazwisko);
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

    @FXML
    private void addEmployeeToTable(MouseEvent event) {
        String selectedEmployee = pracownik.getValue();
        if (selectedEmployee != null && !selectedEmployee.isEmpty()) {
            String[] employeeData = selectedEmployee.split(" ");

            // Tworzenie obiektu reprezentującego pracownika
            Employee employee = new Employee(employeeData[0], employeeData[1]);

            // Dodawanie pracownika do tabeli
            tableView.getItems().add(employee);
        }
    }
}
