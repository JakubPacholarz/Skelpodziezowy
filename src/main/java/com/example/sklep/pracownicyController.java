package com.example.sklep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class pracownicyController implements Initializable {

    @FXML
    private TableView<Employee> tableView;

    @FXML
    private TableColumn<Employee, String> imieColumn;

    @FXML
    private TableColumn<Employee, String> nazwiskoColumn;

    @FXML
    private TableColumn<Employee, String> kontaktColumn;

    @FXML
    private Label label_sklep;

    @FXML
    private Button btnDodajPracownika;

    @FXML
    private Button btnUsunPracownika;

    private String userRole;
    private Connection connectDB;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imieColumn.setCellValueFactory(new PropertyValueFactory<>("imie"));
        nazwiskoColumn.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        kontaktColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Set custom cell factories to center-align the text
        setColumnCellFactory(imieColumn);
        setColumnCellFactory(nazwiskoColumn);
        setColumnCellFactory(kontaktColumn);

        ObservableList<Employee> employeeList = FXCollections.observableArrayList();

        setUserRole(UserSession.getInstance().getUserRole());
        userRole = UserSession.getInstance().getUserRole();

        DatabaseConnection connectNow = new DatabaseConnection();
        connectDB = connectNow.getConnection();

        String connectQuery = "SELECT imie, nazwisko, email FROM users WHERE rola NOT LIKE 'admin'";
        updateButtonVisibility();

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            while (queryOutput.next()) {
                String imie = queryOutput.getString("imie");
                String nazwisko = queryOutput.getString("nazwisko");
                String email = queryOutput.getString("email");

                employeeList.add(new Employee(imie, nazwisko, email, null, null));
            }

            tableView.setItems(employeeList);

        } catch (SQLException e) {
            handleSQLException(e);
        }

        updateButtonVisibility();
    }

    private void setColumnCellFactory(TableColumn<Employee, String> column) {
        column.setCellFactory(new Callback<TableColumn<Employee, String>, TableCell<Employee, String>>() {
            @Override
            public TableCell<Employee, String> call(TableColumn<Employee, String> param) {
                return new TableCell<Employee, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void goToMainPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_sklep.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void setUserRole(String rola) {
        UserSession.getInstance().setUserRole(rola);
        this.userRole = rola;
    }

    private void updateButtonVisibility() {
        boolean isAdmin = "admin".equalsIgnoreCase(userRole);
        btnDodajPracownika.setVisible(isAdmin);
        btnUsunPracownika.setVisible(isAdmin);
    }

    @FXML
    private void handleAddEmployee() {
        try {
            if (!"admin".equalsIgnoreCase(userRole)) {
                showUnauthorizedAlert();
                return;
            }

            Dialog<Employee> dialog = new Dialog<>();
            dialog.setTitle("Dodaj nowego pracownika");
            dialog.setHeaderText("Dodawanie nowego pracownika");

            Label nameLabel = new Label("Imię:");
            TextField nameTextField = new TextField();

            Label surnameLabel = new Label("Nazwisko:");
            TextField surnameTextField = new TextField();

            Label contactLabel = new Label("Email:");
            TextField contactTextField = new TextField();

            Label passwordLabel = new Label("Hasło:");
            PasswordField passwordTextField = new PasswordField();

            Label roleLabel = new Label("Rola:");
            ComboBox<String> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll("pracownik", "kierownik", "admin");

            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(nameLabel, nameTextField, surnameLabel, surnameTextField, contactLabel, contactTextField,
                    passwordLabel, passwordTextField, roleLabel, roleComboBox);
            dialog.getDialogPane().setContent(vbox);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    String selectedRole = roleComboBox.getValue();
                    return new Employee(nameTextField.getText(), surnameTextField.getText(),
                            contactTextField.getText(), passwordTextField.getText(), selectedRole);
                }
                return null;
            });

            Optional<Employee> result = dialog.showAndWait();
            result.ifPresent(employee -> {
                if (isEmailAlreadyRegistered(employee.getEmail())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Email już istnieje");
                    alert.setHeaderText("Nie można dodać pracownika");
                    alert.setContentText("Podany adres e-mail jest już zarejestrowany w systemie.");
                    alert.showAndWait();
                    return;
                }

                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Potwierdzenie dodania");
                confirmationAlert.setHeaderText("Dodanie Pracownika");
                confirmationAlert.setContentText("Czy na pewno chcesz dodać pracownika: " + employee.getImie() + " " + employee.getNazwisko() +
                        " z kontaktem " + employee.getEmail() + " i rolą " + employee.getRola() + "?");

                Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();
                if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                    try {
                        employee.addToDatabase(connectDB);
                        tableView.getItems().add(employee);
                        System.out.println("Dodano pracownika: " + employee.getImie() + " " + employee.getNazwisko() +
                                " z kontaktem " + employee.getEmail() + " i rolą " + employee.getRola());
                    } catch (SQLException e) {
                        handleSQLException(e);
                    }
                } else {
                    System.out.println("Anulowano dodawanie pracownika.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isEmailAlreadyRegistered(String email) {
        try {
            PreparedStatement statement = connectDB.prepareStatement("SELECT * FROM users WHERE email = ?");
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Jeśli wynik zapytania nie jest pusty, email jest już zarejestrowany
        } catch (SQLException e) {
            handleSQLException(e);
            return true; // Załóżmy że wystąpił błąd - dla bezpieczeństwa zwracamy true
        }
    }

    @FXML
    private void handleDelEmployee() {
        try {
            if (!"admin".equalsIgnoreCase(userRole)) {
                showUnauthorizedAlert();
                return;
            }

            Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Potwierdzenie usunięcia");
                confirmationAlert.setHeaderText("Usunięcie Pracownika");
                confirmationAlert.setContentText("Czy na pewno chcesz usunąć pracownika: " + selectedEmployee.getImie() + " " + selectedEmployee.getNazwisko() + "?");

                Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();
                if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                    String deleteQuery = "DELETE FROM users WHERE imie = ? AND nazwisko = ? AND email = ?";
                    try {
                        PreparedStatement deleteStatement = connectDB.prepareStatement(deleteQuery);
                        deleteStatement.setString(1, selectedEmployee.getImie());
                        deleteStatement.setString(2, selectedEmployee.getNazwisko());
                        deleteStatement.setString(3, selectedEmployee.getEmail());
                        deleteStatement.executeUpdate();

                        tableView.getItems().remove(selectedEmployee);
                        System.out.println("Usunięto pracownika: " + selectedEmployee.getImie() + " " + selectedEmployee.getNazwisko());
                    } catch (SQLException e) {
                        handleSQLException(e);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUnauthorizedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Brak autoryzacji");
        alert.setHeaderText("Nie masz wystarczających uprawnień");
        alert.setContentText("Ta operacja wymaga uprawnień administratora.");
        alert.showAndWait();
    }

    private void handleSQLException(SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd SQL");
        alert.setHeaderText("Wystąpił błąd podczas operacji na bazie danych");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
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
