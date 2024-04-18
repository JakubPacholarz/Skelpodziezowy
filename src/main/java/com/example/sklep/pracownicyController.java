package com.example.sklep;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class pracownicyController {

    @FXML
    private Label label1;

    @FXML
    void connect_btn(ActionEvent event) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String connectQuery = "SELECT username FROM users";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            StringBuilder users = new StringBuilder();
            while (queryOutput.next()) {
                users.append(queryOutput.getString("username")).append("\n");
            }

            label1.setText(users.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
