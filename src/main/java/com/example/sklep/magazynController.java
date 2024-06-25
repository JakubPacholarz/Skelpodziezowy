package com.example.sklep;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class magazynController implements Initializable {

    @FXML
    private TableView<Item> tableView;

    @FXML
    private TableColumn<Item, String> dostawcaColumn;

    @FXML
    private TableColumn<Item, String> zasobyColumn;

    @FXML
    private TableColumn<Item, String> kontaktColumn;

    @FXML
    private Label label_sklep;

    @FXML
    private Button addSupplierButton;
    @FXML
    private Button editSupplierButton;
    @FXML
    private Button deleteSupplierButton;
    DatabaseConnection connectNow = new DatabaseConnection();
    Connection connectDB = connectNow.getConnection();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dostawcaColumn.setCellValueFactory(new PropertyValueFactory<>("dostawca"));
        zasobyColumn.setCellValueFactory(new PropertyValueFactory<>("zasoby"));
        kontaktColumn.setCellValueFactory(new PropertyValueFactory<>("kontakt"));

        ObservableList<Item> itemList = FXCollections.observableArrayList();

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String connectQuery = "SELECT dostawca, zasoby, kontakt FROM magazyn";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            while (queryOutput.next()) {
                String dostawca = queryOutput.getString("dostawca");
                String zasoby = queryOutput.getString("zasoby");
                String kontakt = queryOutput.getString("kontakt");

                itemList.add(new Item(dostawca, zasoby, kontakt));
            }

            tableView.setItems(itemList);

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
    private void addSupplier(MouseEvent event) {
        TextField dostawcaField = new TextField();
        TextField zasobyField = new TextField();
        TextField kontaktField = new TextField();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Dodaj Dostawcę");
        alert.setHeaderText("Wprowadź dane nowego dostawcy:");
        alert.getDialogPane().setContent(new VBox(
                new Label("Dostawca:"), dostawcaField,
                new Label("Zasoby:"), zasobyField,
                new Label("Kontakt:"), kontaktField
        ));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String dostawca = dostawcaField.getText();
                String zasoby = zasobyField.getText();
                String kontakt = kontaktField.getText();

                if (dostawca.isEmpty() || zasoby.isEmpty() || kontakt.isEmpty()) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Błąd");
                    errorAlert.setContentText("Wszystkie pola muszą być wypełnione.");
                    errorAlert.showAndWait();
                } else {

                    String insertQuery = "INSERT INTO magazyn (dostawca, zasoby, kontakt) VALUES ('" + dostawca + "', '" + zasoby + "', '" + kontakt + "')";

                    try {
                        Statement statement = connectDB.createStatement();
                        statement.executeUpdate(insertQuery);

                        tableView.getItems().add(new Item(dostawca, zasoby, kontakt));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @FXML
    private void editSupplier(MouseEvent event) {
        Item selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TextField dostawcaField = new TextField(selectedItem.getDostawca());
            TextField zasobyField = new TextField(selectedItem.getZasoby());
            TextField kontaktField = new TextField(selectedItem.getKontakt());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edytuj Dostawcę");
            alert.setHeaderText("Edytuj dane dostawcy:");
            alert.getDialogPane().setContent(new VBox(
                    new Label("Dostawca:"), dostawcaField,
                    new Label("Zasoby:"), zasobyField,
                    new Label("Kontakt:"), kontaktField
            ));

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String dostawca = dostawcaField.getText();
                    String zasoby = zasobyField.getText();
                    String kontakt = kontaktField.getText();

                    if (dostawca.isEmpty() || zasoby.isEmpty() || kontakt.isEmpty()) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Błąd");
                        errorAlert.setContentText("Wszystkie pola muszą być wypełnione.");
                        errorAlert.showAndWait();
                    } else {
                        String updateQuery = "UPDATE magazyn SET dostawca = ?, zasoby = ?, kontakt = ? WHERE dostawca = ? AND zasoby = ? AND kontakt = ?";

                        try {
                            PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);
                            updateStatement.setString(1, dostawca);
                            updateStatement.setString(2, zasoby);
                            updateStatement.setString(3, kontakt);
                            updateStatement.setString(4, selectedItem.getDostawca());
                            updateStatement.setString(5, selectedItem.getZasoby());
                            updateStatement.setString(6, selectedItem.getKontakt());
                            updateStatement.executeUpdate();

                            selectedItem.setDostawca(dostawca);
                            selectedItem.setZasoby(zasoby);
                            selectedItem.setKontakt(kontakt);
                            tableView.refresh();

                            System.out.println("Zaktualizowano dostawcę: " + dostawca);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Brak zaznaczenia");
            alert.setHeaderText("Nie wybrano dostawcy");
            alert.setContentText("Proszę zaznaczyć dostawcę do edycji.");
            alert.showAndWait();
        }
    }

    @FXML
    private void deleteSupplier(MouseEvent event) {
        Item selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Potwierdzenie usunięcia");
            confirmationAlert.setHeaderText("Usunięcie Dostawcy");
            confirmationAlert.setContentText("Czy na pewno chcesz usunąć dostawcę: " + selectedItem.getDostawca() + "?");

            Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();
            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                // Usuń dostawcę z bazy danych
                String deleteQuery = "DELETE FROM magazyn WHERE dostawca = ? AND zasoby = ? AND kontakt = ?";
                try {
                    PreparedStatement deleteStatement = connectDB.prepareStatement(deleteQuery);
                    deleteStatement.setString(1, selectedItem.getDostawca());
                    deleteStatement.setString(2, selectedItem.getZasoby());
                    deleteStatement.setString(3, selectedItem.getKontakt());
                    deleteStatement.executeUpdate();

                    // Usuń dostawcę z tabeli
                    tableView.getItems().remove(selectedItem);
                    System.out.println("Usunięto dostawcę: " + selectedItem.getDostawca());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Anulowano usunięcie dostawcy.");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Brak zaznaczenia");
            alert.setHeaderText("Nie wybrano dostawcy");
            alert.setContentText("Proszę zaznaczyć dostawcę do usunięcia.");
            alert.showAndWait();
        }
    }

    public static class Item {
        private String dostawca;
        private String zasoby;
        private String kontakt;

        public Item(String dostawca, String zasoby, String kontakt) {
            this.dostawca = dostawca;
            this.zasoby = zasoby;
            this.kontakt = kontakt;
        }

        public String getDostawca() {
            return dostawca;
        }

        public void setDostawca(String dostawca) {
            this.dostawca = dostawca;
        }

        public String getZasoby() {
            return zasoby;
        }

        public void setZasoby(String zasoby) {
            this.zasoby = zasoby;
        }

        public String getKontakt() {
            return kontakt;
        }

        public void setKontakt(String kontakt) {
            this.kontakt = kontakt;
        }
    }
}
