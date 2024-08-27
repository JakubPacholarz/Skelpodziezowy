package com.example.sklep;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class SummaryController implements Initializable {
    @FXML
    private Label label_sklep;

    @FXML
    private TableView<SummaryEntry> summaryTable;

    @FXML
    private TableColumn<SummaryEntry, String> columnImie;

    @FXML
    private TableColumn<SummaryEntry, String> columnNazwisko;

    @FXML
    private TableColumn<SummaryEntry, String> columnZadanie;

    @FXML
    private TableColumn<SummaryEntry, String> columnCzas;

    private ObservableList<SummaryEntry> summaryEntries = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTable();
        loadSummaryData();
    }

    private void initializeTable() {
        columnImie.setCellValueFactory(new PropertyValueFactory<>("imie"));
        columnNazwisko.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        columnZadanie.setCellValueFactory(new PropertyValueFactory<>("zadanie"));
        columnCzas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDuration()));

        columnImie.setCellFactory(tc -> new TableCell<SummaryEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER); // Wyśrodkowanie tekstu w komórce
                }
            }
        });

        columnNazwisko.setCellFactory(tc -> new TableCell<SummaryEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER); // Wyśrodkowanie tekstu w komórce
                }
            }
        });

        columnZadanie.setCellFactory(tc -> new TableCell<SummaryEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER); // Wyśrodkowanie tekstu w komórce
                }
            }
        });

        columnCzas.setCellFactory(tc -> new TableCell<SummaryEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER); // Wyśrodkowanie tekstu w komórce
                }
            }
        });

        summaryTable.setItems(summaryEntries);
    }

    private void loadSummaryData() {
        // Pobieranie danych z bazy danych
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String taskQuery = "SELECT u.imie, u.nazwisko, t.task_name, ut.start_date, ut.end_date, ut.status_id " +
                "FROM user_tasks ut " +
                "JOIN users u ON ut.user_id = u.user_id " +
                "JOIN tasks t ON ut.task_id = t.task_id";

        try (PreparedStatement statement = connectDB.prepareStatement(taskQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String imie = resultSet.getString("imie");
                String nazwisko = resultSet.getString("nazwisko");
                String taskName = resultSet.getString("task_name");
                Timestamp startDate = resultSet.getTimestamp("start_date");
                Timestamp completionDate = resultSet.getTimestamp("end_date");
                int statusId = resultSet.getInt("status_id");

                // Check if startDate or completionDate is null
                if (startDate == null || completionDate == null) {
                    // Handle the case where either start date or completion date is missing
                    continue; // Skip this entry and move to the next one
                }

                // Convert Timestamp to LocalDateTime
                LocalDateTime startTime = startDate.toLocalDateTime();
                LocalDateTime endTime = completionDate.toLocalDateTime();

                // Calculate duration as a Duration object
                Duration duration = Duration.between(startTime, endTime);

                // Create SummaryEntry and add to summaryEntries
                SummaryEntry entry = new SummaryEntry(imie, nazwisko, taskName, duration);
                summaryEntries.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToMainPage(MouseEvent event) {
        try {
            // Ładowanie widoku głównej strony
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_sklep.getScene().getWindow();
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
            stage.show(); // Wyświetlenie nowej sceny
        } catch (IOException e) {
            handleIOException(e); // Obsługa IOException przy ładowaniu widoku
        }
    }

    private void handleIOException(IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd wejścia/wyjścia");
        alert.setHeaderText("Wystąpił błąd wejścia/wyjścia");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
