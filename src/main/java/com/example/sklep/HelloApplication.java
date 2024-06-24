package com.example.sklep;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Główna klasa aplikacji JavaFX.
 */
public class HelloApplication extends Application {

    /**
     * Metoda start jest punktem wejścia do aplikacji JavaFX.
     *
     * @param stage główna scena aplikacji
     * @throws IOException jeśli nie uda się załadować pliku FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Ładowanie pliku FXML
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("logowanie.fxml"));

        // Tworzenie sceny z załadowanego FXML i ustalenie jej rozmiaru
        Scene scene = new Scene(fxmlLoader.load(), 700, 700);

        // Ustawianie tytułu okna
        stage.setTitle("Aplikacja!");

        // Ustawianie sceny na scenę główną i wyświetlanie jej
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Główna metoda uruchamiająca aplikację.
     *
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        launch(); // Uruchamianie aplikacji JavaFX
    }
}
