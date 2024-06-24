package com.example.sklep;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    private ChoiceBox<String> choiceBoxZadania; // Nowy ChoiceBox dla zadań

    @FXML
    private Button utworzButton; // Przycisk "Utwórz"

    @FXML
    private TableView<Employee> tableView;

    @FXML
    private TableColumn<Employee, String> columnImie;

    @FXML
    private TableColumn<Employee, String> columnNazwisko;

    @FXML
    private TableColumn<Employee, String> columnTimer;

    @FXML
    private TableColumn<Employee, Void> columnStart; // Kolumna statusu

    @FXML
    private TableColumn<Employee, String> columnTask; // Kolumna z zadaniem

    private static ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private ObservableList<String> zadaniaList = FXCollections.observableArrayList();
    private StringProperty selectedTask = new SimpleStringProperty(""); // Pole do przechowywania wybranego zadania

    public static ObservableList<Employee> getEmployeeList() {
        return employeeList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Utworzenie połączenia z bazą danych
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Zapytanie SQL do pobrania danych pracowników
        String connectQueryEmployees = "SELECT imie, nazwisko FROM pracownik";
        try {
            // Wykonanie zapytania
            Statement statement = connectDB.createStatement();
            ResultSet queryOutputEmployees = statement.executeQuery(connectQueryEmployees);

            // Dodanie wyników zapytania do listy rozwijanej pracowników
            while (queryOutputEmployees.next()) {
                String imie = queryOutputEmployees.getString("imie");
                String nazwisko = queryOutputEmployees.getString("nazwisko");
                pracownik.getItems().add(imie + " " + nazwisko);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Obsługa wyjątków
        }

        // Zapytanie SQL do pobrania danych zadań
        String connectQueryTasks = "SELECT task_name FROM tasks";
        try {
            // Wykonanie zapytania
            Statement statement = connectDB.createStatement();
            ResultSet queryOutputTasks = statement.executeQuery(connectQueryTasks);

            // Dodanie wyników zapytania do listy zadań w choiceBoxZadania
            while (queryOutputTasks.next()) {
                String zadanie = queryOutputTasks.getString("task_name");
                choiceBoxZadania.getItems().add(zadanie);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Obsługa wyjątków
        }

        // Ustawienie kolumn tabeli pracowników
        columnImie.setCellValueFactory(new PropertyValueFactory<>("imie"));
        columnNazwisko.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        columnTimer.setCellValueFactory(new PropertyValueFactory<>("timerDisplay"));
        columnTask.setCellValueFactory(new PropertyValueFactory<>("selectedTask"));

        // Ustawienie komórki dla kolumny columnStart
        columnStart.setCellFactory(new Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>>() {
            @Override
            public TableCell<Employee, Void> call(TableColumn<Employee, Void> param) {
                return new TableCell<Employee, Void>() {
                    private final Button startStopButton = new Button("Start");

                    {
                        startStopButton.setOnAction(event -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            if (!employee.isTaskCompleted()) {
                                employee.startOrStopTimer(); // Startuje lub zatrzymuje timer w zależności od stanu
                                updateButtonLabel(employee);
                            }
                        });
                    }

                    private void updateButtonLabel(Employee employee) {
                        if (employee.isTimerRunning()) {
                            startStopButton.setText("Stop");
                        } else if (employee.isTaskCompleted()) {
                            startStopButton.setText("Ukończone");
                            startStopButton.setDisable(true); // Wyłącz przycisk, gdy zadanie jest ukończone
                        } else {
                            startStopButton.setText("Start");
                        }
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Employee employee = getTableView().getItems().get(getIndex());
                            updateButtonLabel(employee);
                            setGraphic(startStopButton);
                        }
                    }
                };
            }
        });

        // Dodanie listenera do ChoiceBox dla zadań
        choiceBoxZadania.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                selectedTask.set(newValue);
            }
        });

        // Dodanie listenera do ChoiceBox dla pracowników
        pracownik.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                // Wyczyszczenie zaznaczonego zadania po zmianie pracownika
                selectedTask.set("");
            }
        });

        // Dodanie akcji dla przycisku "Utwórz"
        utworzButton.setOnAction(event -> handleUtworzButton(event)); // Delegowanie do metody handleUtworzButton
    }

    @FXML
    private void handleUtworzButton(ActionEvent event) {
        String selectedEmployee = pracownik.getValue();
        String selectedTaskName = choiceBoxZadania.getValue();
        if (selectedEmployee != null && !selectedEmployee.isEmpty() &&
                selectedTaskName != null && !selectedTaskName.isEmpty()) {
            String[] employeeData = selectedEmployee.split(" ");
            Employee employee = new Employee(employeeData[0], employeeData[1]);
            employee.setSelectedTask(selectedTaskName); // Ustawienie wybranego zadania
            employeeList.add(employee);
            tableView.setItems(employeeList); // Ustawienie listy pracowników w tabeli
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
