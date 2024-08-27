package com.example.sklep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminPanelController implements Initializable {

    @FXML
    private ListView<String> userListView;

    private ObservableList<String> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Wczytaj listę niezatwierdzonych użytkowników
        loadUnapprovedUsers();
    }

    private void loadUnapprovedUsers() {
        userList.clear();
        String query = "SELECT email FROM users WHERE approved = false";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String email = resultSet.getString("email");
                userList.add(email);
            }
            userListView.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void approveUserButtonClicked(ActionEvent event) {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Wybierz użytkownika do zatwierdzenia!");
            return;
        }

        // Zaktualizuj w bazie danych
        String query = "UPDATE users SET approved = true WHERE email = ?";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, selectedUser);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert("Użytkownik zatwierdzony!");
                loadUnapprovedUsers(); // Odśwież listę
            } else {
                showAlert("Błąd podczas zatwierdzania użytkownika.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void refreshButtonClicked(ActionEvent event) {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Wybierz użytkownika do usunięcia!");
            return;
        }

        // Usuń użytkownika z bazy danych
        String query = "DELETE FROM users WHERE email = ?";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, selectedUser);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Użytkownik usunięty!");
                userList.remove(selectedUser); // Usuń z listy lokalnej
                userListView.setItems(userList); // Zaktualizuj widok ListView
            } else {
                showAlert("Błąd podczas usuwania użytkownika.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void logoutButtonClicked(ActionEvent event) {
        // Przejdź do strony logowania
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

    public void setUserRole(String userRole) {
    }
}
