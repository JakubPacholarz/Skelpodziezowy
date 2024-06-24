package com.example.sklep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SummaryController implements Initializable {
    @FXML
    private Label label_sklep;

    @FXML
    private TableView<Summary> summaryTable;

    @FXML
    private TableColumn<Summary, String> employeeNameColumn;

    @FXML
    private TableColumn<Summary, String> totalTimeColumn;

    @FXML
    private TableColumn<Summary, String> taskNameColumn;

    private ObservableList<Summary> summaryList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedTotalTime"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));

        Map<String, Map<String, Summary>> employeeTaskSummaryMap = new HashMap<>();

        // Logic to calculate total time worked by employees
        for (Employee employee : zadaniaController.getEmployeeList()) {
            String employeeName = employee.getImie() + " " + employee.getNazwisko();

            // Summing up time for all tasks performed by the employee
            for (Task task : employee.getSelectedTasks()) {
                String taskName = task.getTaskName();
                int totalTimeSeconds = task.getTotalSeconds();

                // Check if there is already a summary entry for this employee and task
                if (employeeTaskSummaryMap.containsKey(employeeName) &&
                        employeeTaskSummaryMap.get(employeeName).containsKey(taskName)) {
                    // Update existing summary entry
                    Summary existingSummary = employeeTaskSummaryMap.get(employeeName).get(taskName);
                    existingSummary.setTotalSeconds(existingSummary.getTotalSeconds() + totalTimeSeconds);
                } else {
                    // Create new summary entry
                    Summary newSummary = new Summary(employeeName, taskName, totalTimeSeconds);
                    summaryList.add(newSummary);

                    // Store the summary entry in the map for future updates
                    if (!employeeTaskSummaryMap.containsKey(employeeName)) {
                        employeeTaskSummaryMap.put(employeeName, new HashMap<>());
                    }
                    employeeTaskSummaryMap.get(employeeName).put(taskName, newSummary);
                }
            }
        }

        summaryTable.setItems(summaryList);
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
}
