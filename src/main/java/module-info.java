module com.example.sklep {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires kernel;
    requires layout;
    opens com.example.sklep to javafx.fxml;
    exports com.example.sklep;
}
