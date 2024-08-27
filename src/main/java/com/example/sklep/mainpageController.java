package com.example.sklep;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class mainpageController implements Initializable {

    @FXML
    private Label label_pracownicy;
    @FXML
    private Label label_magazyn;
    @FXML
    private Label label_zadania;
    @FXML
    private Label label_dostawcy;
    @FXML
    private Label label_informacje;
    @FXML
    private Label label_raporty;
    @FXML
    private Button logoutButton;
    @FXML
    private Label label_role;
    @FXML
    private Label label_user_id;

    private UserSession userSession = UserSession.getInstance();
    private String userRole;
    private int loggedInUserId;
    private FXMLLoader loader; // Deklaracja zmiennej loader

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateUserInfo();
        loggedInUserId = userSession.getLoggedInUserId();
        String userRole = userSession.getUserRole();
        System.out.println("Aktualna rola użytkownika: " + userRole); // Dodaj logowanie

        updateRoleLabel();

        // Ustawienie akcji kliknięcia na etykiety
        label_pracownicy.setOnMouseClicked(event -> goToPage1("pracownicy.fxml"));
        label_magazyn.setOnMouseClicked(event -> goToPage2("magazyn.fxml"));
        label_zadania.setOnMouseClicked(event -> goToPage3("zadania.fxml"));
        label_dostawcy.setOnMouseClicked(event -> goToPage4("podsumowanie.fxml"));
        label_informacje.setOnMouseClicked(event -> goToPage5("informacje.fxml"));
        label_raporty.setOnMouseClicked(event -> goToPage6("raporty.fxml"));

        if ("admin".equals(userRole)) {
            label_informacje.setVisible(true); // Make the label visible
        } else {
            label_informacje.setVisible(false); // Hide the label
        }
    }


    @FXML
    void logoutButtonClicked(ActionEvent event) {
        goToLoginPage();
    }

    private void goToLoginPage() {
        try {
            loader = new FXMLLoader(getClass().getResource("logowanie.fxml")); // Inicjalizacja loader w metodzie goToLoginPage()
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToPage1(String pracownicy) {
        try {
            loader = new FXMLLoader(getClass().getResource("pracownicy.fxml")); // Inicjalizacja loader w metodzie goToPage1()
            Parent root = loader.load();

            pracownicyController controller = loader.getController();
            controller.setUserRole(userSession.getUserRole());

            Stage stage = (Stage) label_pracownicy.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void goToPage2(String magazyn) {
        try {
            loader = new FXMLLoader(getClass().getResource("magazyn.fxml")); // Inicjalizacja loader w metodzie goToPage2()
            Parent root = loader.load();
            Stage stage = (Stage) label_magazyn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToPage3(String zadania) {
        try {
            loader = new FXMLLoader(getClass().getResource("zadania.fxml")); // Inicjalizacja loader w metodzie goToPage3()
            Parent root = loader.load();
            Stage stage = (Stage) label_zadania.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToPage4(String podsumowanie) {
        try {
            loader = new FXMLLoader(getClass().getResource("podsumowanie.fxml")); // Inicjalizacja loader w metodzie goToPage4()
            Parent root = loader.load();
            Stage stage = (Stage) label_dostawcy.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToPage5(String informacje) {
        // Only allow access if user is admin
        userRole = this.userRole;
        if ("admin".equals(userRole)) {
            try {
                loader = new FXMLLoader(getClass().getResource("informacje.fxml")); // Inicjalizacja loader w metodzie goToPage5()
                Parent root = loader.load();

                // Pass user role to the informacjeController if needed
                AdminPanelController controller = loader.getController();
                controller.setUserRole(userSession.getUserRole());

                Stage stage = (Stage) label_informacje.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle unauthorized access (optional)
            System.out.println("Unauthorized access to informacje page!");
        }
    }



    private void goToPage6(String raporty) {
        try {
            loader = new FXMLLoader(getClass().getResource("raporty.fxml")); // Inicjalizacja loader w metodzie goToPage6()
            Parent root = loader.load();
            Stage stage = (Stage) label_raporty.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateUserInfo() {
        loggedInUserId = userSession.getLoggedInUserId();
        userRole = userSession.getUserRole();
        updateRoleLabel();
        if ("admin".equals(userRole)) {
            label_informacje.setVisible(true);
        } else {
            label_informacje.setVisible(false);
        }
    }
    private void updateRoleLabel() {
        String userRole = userSession.getUserRole();
        System.out.println("Aktualna rola użytkownika: " + userRole); // dodaj logowanie

    }


    public void setUserRole(String userRole) {
        this.userRole = userRole;
        updateRoleLabel(); // Wywołaj metodę do aktualizacji etykiety
    }

}
