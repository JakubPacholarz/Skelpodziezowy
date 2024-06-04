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

public class infController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label label_sklep1;

    @FXML
    private TextArea opisSklepuTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDropShadowEffect();

        // Twój kod dołączający opis sklepu
        String opisSklepu = "Witaj w naszym sklepie odzieżowym!\n\n" +
                "Jesteśmy dumnymi dostawcami najnowszych trendów w modzie. W naszym asortymencie znajdziesz najwyższej jakości ubrania dla kobiet, mężczyzn i dzieci. Od casualowej elegancji po sportowy styl - mamy wszystko, czego potrzebujesz, aby wyrazić swój indywidualny styl!\n\n" +
                "Nie tylko oferujemy wyjątkowe ubrania, ale także zapewniamy niezrównaną obsługę klienta. Nasz zespół ekspertów zawsze jest gotowy, aby doradzić Ci w doborze odpowiednich ubrań i akcesoriów.\n\n" +
                "Przyjdź do naszego sklepu i odkryj świat mody, który spełni Twoje najśmielsze oczekiwania!";
        opisSklepuTextArea.setText(opisSklepu);
    }

    private void addDropShadowEffect() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(112, 112, 112));
        dropShadow.setRadius(10);
        dropShadow.setSpread(0.5);
        anchorPane.setEffect(dropShadow);
    }

    @FXML
    private void goToMainPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) label_sklep1.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
