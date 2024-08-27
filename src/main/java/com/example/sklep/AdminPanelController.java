package com.example.sklep;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kontroler dla widoku informacyjnego sklepu.
 */
public class infController implements Initializable {

    @FXML
    private AnchorPane anchorPane; // Główny kontener dla komponentów FXML

    @FXML
    private Label label_sklep1; // Etykieta sklepu

    @FXML
    private TextArea opisSklepuTextArea; // Pole tekstowe dla opisu sklepu

    /**
     * Inicjalizacja kontrolera, wywoływana po załadowaniu FXML.
     *
     * @param location  lokalizacja zasobu
     * @param resources zasoby
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDropShadowEffect(); // Dodanie efektu cienia

        // Dodanie opisu sklepu do pola tekstowego
        String opisSklepu = "Witaj w naszym sklepie odzieżowym!\n\n" +
                "Jesteśmy dumnymi dostawcami najnowszych trendów w modzie. W naszym asortymencie znajdziesz najwyższej jakości ubrania dla kobiet, mężczyzn i dzieci. Od casualowej elegancji po sportowy styl - mamy wszystko, czego potrzebujesz, aby wyrazić swój indywidualny styl!\n\n" +
                "Nie tylko oferujemy wyjątkowe ubrania, ale także zapewniamy niezrównaną obsługę klienta. Nasz zespół ekspertów zawsze jest gotowy, aby doradzić Ci w doborze odpowiednich ubrań i akcesoriów.\n\n" +
                "Przyjdź do naszego sklepu i odkryj świat mody, który spełni Twoje najśmielsze oczekiwania!";
        opisSklepuTextArea.setText(opisSklepu); // Ustawienie tekstu w polu tekstowym
    }

    /**
     * Dodaje efekt cienia do głównego kontenera.
     */
    private void addDropShadowEffect() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(112, 112, 112)); // Ustawienie koloru cienia
        dropShadow.setRadius(10); // Ustawienie promienia cienia
        dropShadow.setSpread(0.5); // Ustawienie rozprzestrzenienia cienia
        anchorPane.setEffect(dropShadow); // Dodanie efektu do kontenera
    }

    /**
     * Przechodzi do głównej strony aplikacji po kliknięciu na etykietę.
     *
     * @param event zdarzenie kliknięcia myszą
     */
    @FXML
    private void goToMainPage(MouseEvent event) {
        try {
            // Ładowanie widoku głównej strony
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_sklep1.getScene().getWindow(); // Pobranie bieżącej sceny
            stage.setScene(new Scene(root)); // Ustawienie nowej sceny
            stage.show(); // Wyświetlenie sceny
        } catch (Exception e) {
            e.printStackTrace(); // Wyświetlenie informacji o błędzie
        }
    }
}
