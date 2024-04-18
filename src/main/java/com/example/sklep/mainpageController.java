package com.example.sklep;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class mainpageController {

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        // Dodajemy obsługę zdarzenia dla przycisku wylogowania
        logoutButton.setOnAction(event -> {
            // Pobierz obecne okno
            Stage stage = (Stage) logoutButton.getScene().getWindow();

            // Zamknij obecne okno
            stage.close();

            // Otwórz nowe okno logowania
            openLoginWindow();
        });
    }

    private void openLoginWindow() {
        try {
            // Załaduj plik FXML dla okna logowania
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logowanie.fxml"));
            Parent root = loader.load();

            // Ustaw kontroler dla okna logowania
            LoginController controller = loader.getController();

            // Utwórz nowe okno
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
