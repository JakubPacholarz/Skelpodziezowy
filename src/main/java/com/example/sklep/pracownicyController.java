package com.example.sklep;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Kontroler dla widoku pracowników.
 */
public class pracownicyController implements Initializable {

    @FXML
    private TableView<Employee> tableView; // Tabela do wyświetlania danych pracowników

    @FXML
    private TableColumn<Employee, String> imieColumn;

    @FXML
    private TableColumn<Employee, String> nazwiskoColumn;

    @FXML
    private TableColumn<Employee, String> kontaktColumn;

    @FXML
    private Label label_sklep; // Etykieta sklepu, używana do nawigacji
    DatabaseConnection connectNow = new DatabaseConnection();
    Connection connectDB = connectNow.getConnection();


    /**
     * Metoda inicjalizacyjna, wywoływana po załadowaniu FXML.
     *
     * @param location  lokalizacja pliku FXML
     * @param resources zasoby do wykorzystania w kontrolerze
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imieColumn.setCellValueFactory(new PropertyValueFactory<>("imie"));
        nazwiskoColumn.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        kontaktColumn.setCellValueFactory(new PropertyValueFactory<>("kontakt"));

        ObservableList<Employee> employeeList = FXCollections.observableArrayList();

        // Tworzenie połączenia z bazą danych
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Zapytanie do bazy danych o pracowników
        String connectQuery = "SELECT imie, nazwisko, kontakt FROM pracownik";

        try {
            // Wykonanie zapytania
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            while (queryOutput.next()) {
                // Pobranie danych pracowników
                String imie = queryOutput.getString("imie");
                String nazwisko = queryOutput.getString("nazwisko");
                String kontakt = queryOutput.getString("kontakt");

                employeeList.add(new Employee(imie, nazwisko, kontakt));
            }

            tableView.setItems(employeeList);

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

    @FXML
    private void handleAddEmployee() {
        try {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Dodaj nowego pracownika");
            dialog.setHeaderText("Dodawanie nowego pracownika");

            // Create labels and text fields
            Label nameLabel = new Label("Imię:");
            TextField nameTextField = new TextField();

            Label surnameLabel = new Label("Nazwisko:");
            TextField surnameTextField = new TextField();

            Label contactLabel = new Label("Kontakt:");
            TextField contactTextField = new TextField();

            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(nameLabel, nameTextField, surnameLabel, surnameTextField, contactLabel, contactTextField);
            dialog.getDialogPane().setContent(vbox);

            // Add buttons
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Handle OK button action
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    return nameTextField.getText() + ";" + surnameTextField.getText() + ";" + contactTextField.getText();
                }
                return null;
            });

            // Show dialog and process result
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(input -> {
                String[] parts = input.split(";");
                if (parts.length == 3) {
                    String name = parts[0];
                    String surname = parts[1];
                    String contact = parts[2];

                    // Example of confirming insertion
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Potwierdzenie dodania");
                    confirmationAlert.setHeaderText("Dodanie Pracownika");
                    confirmationAlert.setContentText("Czy na pewno chcesz dodać pracownika: " + name + " " + surname + " z kontaktem " + contact + "?");

                    Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();
                    if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                        // Wstaw nowego pracownika do bazy danych
                        String insertQuery = "INSERT INTO pracownik (imie, nazwisko, kontakt) VALUES (?, ?, ?)";
                        PreparedStatement insertStatement = null;
                        try {
                            insertStatement = connectDB.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            insertStatement.setString(1, name);
                            insertStatement.setString(2, surname);
                            insertStatement.setString(3, contact);
                            insertStatement.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Dodano pracownika: " + name + " " + surname + " z kontaktem " + contact);
                        tableView.getItems().add(new pracownicyController.Employee(name, surname, contact));
                    } else {
                        System.out.println("Anulowano dodawanie pracownika.");
                    }
                } else {
                    System.out.println("Błąd podczas przetwarzania danych.");
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDelEmployee() {
        Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Potwierdzenie usunięcia");
            confirmationAlert.setHeaderText("Usunięcie Pracownika");
            confirmationAlert.setContentText("Czy na pewno chcesz usunąć pracownika: " + selectedEmployee.getImie() + " " + selectedEmployee.getNazwisko() + "?");

            Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();
            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                // Usuń pracownika z bazy danych
                String deleteQuery = "DELETE FROM pracownik WHERE imie = ? AND nazwisko = ? AND kontakt = ?";
                try {
                    PreparedStatement deleteStatement = connectDB.prepareStatement(deleteQuery);
                    deleteStatement.setString(1, selectedEmployee.getImie());
                    deleteStatement.setString(2, selectedEmployee.getNazwisko());
                    deleteStatement.setString(3, selectedEmployee.getKontakt());
                    deleteStatement.executeUpdate();

                    // Usuń pracownika z tabeli
                    tableView.getItems().remove(selectedEmployee);
                    System.out.println("Usunięto pracownika: " + selectedEmployee.getImie() + " " + selectedEmployee.getNazwisko());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Anulowano usunięcie pracownika.");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Brak zaznaczenia");
            alert.setHeaderText("Nie wybrano pracownika");
            alert.setContentText("Proszę zaznaczyć pracownika do usunięcia.");
            alert.showAndWait();
        }
    }
    public static class Employee {
        private String imie;
        private String nazwisko;
        private String kontakt;

        public Employee(String imie, String nazwisko, String kontakt) {
            this.imie = imie;
            this.nazwisko = nazwisko;
            this.kontakt = kontakt;
        }

        public String getImie() {
            return imie;
        }

        public String getNazwisko() {
            return nazwisko;
        }

        public String getKontakt() {
            return kontakt;
        }
    }
}
