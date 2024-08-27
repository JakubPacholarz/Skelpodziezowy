package com.example.sklep;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class zadaniaController implements Initializable {
    private Map<Integer, String> statusMap = new HashMap<>();

    @FXML
    private Label label_sklep;

    @FXML
    private ChoiceBox<String> pracownik;

    @FXML
    private ChoiceBox<String> choiceBoxZadania;

    @FXML
    private Button utworzButton;

    @FXML
    private TableView<Employee> tableView;

    @FXML
    private TableColumn<Employee, String> columnImie;

    @FXML
    private TableColumn<Employee, String> columnNazwisko;

    @FXML
    private TableColumn<Employee, String> columnTimer;

    @FXML
    private TableColumn<Employee, Void> columnStart;

    @FXML
    private TableColumn<Employee, String> columnTask;

    @FXML
    private TableColumn<Employee, String> columnStatus;

    @FXML
    private TableColumn<Employee, String> columnCompletionTime;

    @FXML
    TableColumn<Employee, Void> columnDelete = new TableColumn<>("Delete");
    @FXML
    private Label labelWybierzPracownika;

    @FXML
    private Label labelWybierzZadanie;

    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private UserSession userSession = UserSession.getInstance();
    private DataManager dataManager = DataManager.getInstance();

    private int loggedInUserId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loggedInUserId = userSession.getLoggedInUserId();
        String userRole = userSession.getUserRole();
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        // Load statuses from database into statusMap
        loadStatuses(connectDB);
        setColumnCellFactory(columnImie);
        setColumnCellFactory(columnNazwisko);
        setColumnCellFactory(columnTimer);
        setColumnCellFactory(columnTask);
        setColumnCellFactory(columnStatus);
        setColumnCellFactory(columnCompletionTime);


        if ("pracownik".equals(userRole)) {
            pracownik.setVisible(false);
            choiceBoxZadania.setVisible(false);
            utworzButton.setVisible(false);
            labelWybierzPracownika.setVisible(false);
            labelWybierzZadanie.setVisible(false);
            loadEmployeeTasks(loggedInUserId);
        } else if ("admin".equals(userRole) || "kierownik".equals(userRole)) {
            loadEmployees(connectDB);
            loadTasks(connectDB);
            loadAllEmployeeTasks();
        }

        // Set cell factory for columnStart
        columnStart.setCellFactory(createStartButtonCellFactory(userRole));

        // Add delete column for admin and kierownik
        if ("admin".equals(userRole) || "kierownik".equals(userRole)) {
            columnDelete.setCellFactory(createDeleteButtonCellFactory());
        }

        // Set cell value factories for other columns
        columnImie.setCellValueFactory(new PropertyValueFactory<>("imie"));
        columnNazwisko.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        columnTimer.setCellValueFactory(cellData -> {
            int statusId = cellData.getValue().getStatusId();
            if (statusId == 2 || statusId == 3) {
                return new SimpleStringProperty(cellData.getValue().getTimerDisplay());
            }
            return null;
        });

        columnTask.setCellValueFactory(new PropertyValueFactory<>("selectedTask"));

        columnCompletionTime.setCellValueFactory(cellData -> {
            int statusId = cellData.getValue().getStatusId();
            if (statusId != 1 && statusId != 2) {
                return new SimpleStringProperty(cellData.getValue().getCompletionTimeDisplay());
            }
            return null;
        });

        columnDelete.setCellValueFactory(new PropertyValueFactory<>(""));
        if ("admin".equals(userRole) || "kierownik".equals(userRole)) {
            columnDelete.setCellFactory(createDeleteButtonCellFactory());
        }
        columnStatus.setCellValueFactory(cellData -> {
            int statusId = cellData.getValue().getStatusId();
            return new SimpleStringProperty(statusMap.getOrDefault(statusId, "Unknown"));
        });

        // Set table data
        tableView.setItems(employeeList);
    }

    // Method to load statuses from taskstatus table
    private void loadStatuses(Connection connectDB) {
        String statusQuery = "SELECT * FROM taskstatus";
        try (PreparedStatement statement = connectDB.prepareStatement(statusQuery);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int statusId = resultSet.getInt("status_id");
                String statusName = resultSet.getString("status_name");
                statusMap.put(statusId, statusName); // Populate statusMap with status names
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to create cell factory for columnStart
    private Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>> createStartButtonCellFactory(String userRole) {
        return new Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>>() {
            @Override
            public TableCell<Employee, Void> call(TableColumn<Employee, Void> param) {
                return new StartButtonCell(userRole);
            }
        };
    }

    private class StartButtonCell extends TableCell<Employee, Void> {
        private final Button startStopButton = new Button("Rozpocznij");
        private final String userRole;

        StartButtonCell(String userRole) {
            this.userRole = userRole;

            // Sprawdź rolę użytkownika
            if (!"admin".equals(userRole) && !"kierownik".equals(userRole)) {
                startStopButton.setOnAction(event -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    int status = employee.getStatusId();
                    System.out.println(status);

                    if (status == 1) {
                        // Set the start date of the task
                        // employee.setStartDate(LocalDate.now());
                        // Update button label and status
                        updateButtonLabel(employee);
                        // Save start date to database
                        saveStartDateToDatabase(employee);
                        // Update status_id in database
                        updateTaskStatusInDatabase(employee, 2); // Aktualizacja status_id na 2
                        employeeList.clear();
                        loadEmployeeTasks(loggedInUserId);



                        // Update timer display

                    } else if (status == 2) {
                        // Set the completion date of the task
                        // employee.setCompletionDate(LocalDate.now());
                        // Update button label and status
                        updateButtonLabel(employee);
                        // Save completion date to database
                        saveCompletionDateToDatabase(employee);
                        // Set completion time display
                        employee.setCompletionTimeDisplay(LocalTime.now().toString());
                        // Update status_id in database
                        updateTaskStatusInDatabase(employee, 3); // Aktualizacja status_id na 3
                        employeeList.clear();
                        loadEmployeeTasks(loggedInUserId);

                    }else{
                        loadEmployeeTasks(loggedInUserId);
                    }

                    // Refresh table view to reflect changes
                    getTableView().refresh();
                });

                setGraphic(startStopButton);
            }
        }

        private void updateButtonLabel(Employee employee) {
            int status = employee.getStatusId();
            if (status == 2) {
                startStopButton.setText("Zakończ");
                startStopButton.setStyle("-fx-background-color: #FF0000;");
                startStopButton.setDisable(false);

            } else if (status == 3) {
                startStopButton.setText("Ukończone");
                startStopButton.setDisable(true);


            } else {
                startStopButton.setText("Rozpocznij");
                startStopButton.setStyle("-fx-background-color: #00FF00;");
                startStopButton.setDisable(false);

            }
        }

        // Metoda do aktualizacji status_id w bazie danych
        private void updateTaskStatusInDatabase(Employee employee, int newStatusId) {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String userIdQuery = "SELECT user_id FROM users WHERE imie = ? AND nazwisko = ?";
                PreparedStatement userIdStmt = connectDB.prepareStatement(userIdQuery);
                userIdStmt.setString(1, employee.getImie());
                userIdStmt.setString(2, employee.getNazwisko());
                ResultSet userIdResult = userIdStmt.executeQuery();
                int userId = -1;
                if (userIdResult.next()) {
                    userId = userIdResult.getInt("user_id");
                }

                String taskIdQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
                PreparedStatement taskIdStmt = connectDB.prepareStatement(taskIdQuery);
                taskIdStmt.setString(1, employee.getSelectedTask());
                ResultSet taskIdResult = taskIdStmt.executeQuery();
                int taskId = -1;
                if (taskIdResult.next()) {
                    taskId = taskIdResult.getInt("task_id");
                }

                // Update query to set status_id to the new status ID
                String updateQuery = "UPDATE user_tasks SET status_id = ? WHERE user_id = ? AND task_id = ?";
                PreparedStatement updateStmt = connectDB.prepareStatement(updateQuery);
                updateStmt.setInt(1, newStatusId); // Ustawienie nowego status_id
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, taskId);
                updateStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Metoda do zapisania start date w bazie danych
        private void saveStartDateToDatabase(Employee employee) {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String userIdQuery = "SELECT user_id FROM users WHERE imie = ? AND nazwisko = ?";
                PreparedStatement userIdStmt = connectDB.prepareStatement(userIdQuery);
                userIdStmt.setString(1, employee.getImie());
                userIdStmt.setString(2, employee.getNazwisko());
                ResultSet userIdResult = userIdStmt.executeQuery();
                int userId = -1;
                if (userIdResult.next()) {
                    userId = userIdResult.getInt("user_id");
                }

                String taskIdQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
                PreparedStatement taskIdStmt = connectDB.prepareStatement(taskIdQuery);
                taskIdStmt.setString(1, employee.getSelectedTask());
                ResultSet taskIdResult = taskIdStmt.executeQuery();
                int taskId = -1;
                if (taskIdResult.next()) {
                    taskId = taskIdResult.getInt("task_id");
                }

                // Insert query to save start date to database
                String updateQuery = "UPDATE user_tasks SET start_date = NOW() WHERE user_id = ? AND task_id = ?";
                PreparedStatement updateStmt = connectDB.prepareStatement(updateQuery);
                updateStmt.setInt(1, userId);
                updateStmt.setInt(2, taskId);
                updateStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Metoda do zapisania completion date w bazie danych
        private void saveCompletionDateToDatabase(Employee employee) {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String userIdQuery = "SELECT user_id FROM users WHERE imie = ? AND nazwisko = ?";
                PreparedStatement userIdStmt = connectDB.prepareStatement(userIdQuery);
                userIdStmt.setString(1, employee.getImie());
                userIdStmt.setString(2, employee.getNazwisko());
                ResultSet userIdResult = userIdStmt.executeQuery();
                int userId = -1;
                if (userIdResult.next()) {
                    userId = userIdResult.getInt("user_id");
                }

                String taskIdQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
                PreparedStatement taskIdStmt = connectDB.prepareStatement(taskIdQuery);
                taskIdStmt.setString(1, employee.getSelectedTask());
                ResultSet taskIdResult = taskIdStmt.executeQuery();
                int taskId = -1;
                if (taskIdResult.next()) {
                    taskId = taskIdResult.getInt("task_id");
                }

                // Insert query to save completion date to database
                String updateQuery = "UPDATE user_tasks SET end_date = NOW() WHERE user_id = ? AND task_id = ?";
                PreparedStatement updateStmt = connectDB.prepareStatement(updateQuery);
                updateStmt.setInt(1, userId);
                updateStmt.setInt(2, taskId);
                updateStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                Employee employee = getTableView().getItems().get(getIndex());
                // Sprawdź rolę użytkownika przed aktualizacją przycisku
                if (!"admin".equals(userRole) && !"kierownik".equals(userRole)) {
                    updateButtonLabel(employee);
                    setGraphic(startStopButton);
                } else {
                    setGraphic(null); // Ukryj przycisk dla admina i kierownika
                }
            }
        }

        private void updateTimerDisplay(Employee employee) {
            employee.setTimerDisplay(LocalTime.now().toString());
        }
    }

    private Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>> createDeleteButtonCellFactory() {
        return new Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>>() {
            @Override
            public TableCell<Employee, Void> call(TableColumn<Employee, Void> param) {
                return new TableCell<Employee, Void>() {
                    private final Button deleteButton = new Button("Usuń");

                    {
                        deleteButton.setOnAction(event -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            deleteTask(employee);
                            employeeList.remove(employee);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        };
    }

    private void deleteTask(Employee employee) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String userIdQuery = "SELECT user_id FROM users WHERE imie = ? AND nazwisko = ?";
            PreparedStatement userIdStmt = connectDB.prepareStatement(userIdQuery);
            userIdStmt.setString(1, employee.getImie());
            userIdStmt.setString(2, employee.getNazwisko());
            ResultSet userIdResult = userIdStmt.executeQuery();
            int userId = -1;
            if (userIdResult.next()) {
                userId = userIdResult.getInt("user_id");
            }

            String taskIdQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
            PreparedStatement taskIdStmt = connectDB.prepareStatement(taskIdQuery);
            taskIdStmt.setString(1, employee.getSelectedTask());
            ResultSet taskIdResult = taskIdStmt.executeQuery();
            int taskId = -1;
            if (taskIdResult.next()) {
                taskId = taskIdResult.getInt("task_id");
            }

            // Delete query to remove the task from user_tasks table
            String deleteQuery = "DELETE FROM user_tasks WHERE user_id = ? AND task_id = ?";
            PreparedStatement deleteStmt = connectDB.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, taskId);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load all employees
    private void loadEmployees(Connection connectDB) {
        String employeeQuery = "SELECT imie, nazwisko FROM users WHERE rola LIKE 'pracownik'";
        try (PreparedStatement statement = connectDB.prepareStatement(employeeQuery);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String imie = resultSet.getString("imie");
                String nazwisko = resultSet.getString("nazwisko");
                pracownik.getItems().add(imie + " " + nazwisko);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load all tasks
    private void loadTasks(Connection connectDB) {
        String taskQuery = "SELECT task_name FROM tasks";
        try (PreparedStatement statement = connectDB.prepareStatement(taskQuery);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String taskName = resultSet.getString("task_name");
                choiceBoxZadania.getItems().add(taskName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load all employee tasks
    private void loadAllEmployeeTasks() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String taskQuery = "SELECT u.imie, u.nazwisko, t.task_name, ut.start_date, ut.end_date, ut.status_id " +
                "FROM user_tasks ut " +
                "JOIN users u ON ut.user_id = u.user_id " +
                "JOIN tasks t ON ut.task_id = t.task_id";

        try (PreparedStatement statement = connectDB.prepareStatement(taskQuery);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String imie = resultSet.getString("imie");
                String nazwisko = resultSet.getString("nazwisko");
                String taskName = resultSet.getString("task_name");
                Timestamp startDate = resultSet.getTimestamp("start_date");
                Timestamp completionDate = resultSet.getTimestamp("end_date");
                int statusId = resultSet.getInt("status_id");

                Employee employee = new Employee(imie, nazwisko, null, null, null);
                employee.setSelectedTask(taskName);
                employee.setStatusId(statusId);

                if (startDate != null) {
                    employee.setTimerDisplay(startDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }

                if (completionDate != null) {
                    employee.setCompletionTimeDisplay(completionDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                tableView.refresh();
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load employee tasks for logged-in user
    private void loadEmployeeTasks(int userId) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String taskQuery = "SELECT ut.start_date, t.task_name, u.imie, u.nazwisko, ts.status_id, ut.end_date FROM user_tasks ut JOIN tasks t ON ut.task_id = t.task_id JOIN users u ON ut.user_id = u.user_id JOIN taskstatus ts ON ut.status_id = ts.status_id WHERE u.user_id = ?";
        try (PreparedStatement taskStmt = connectDB.prepareStatement(taskQuery)) {
            taskStmt.setInt(1, userId);
            ResultSet resultSet = taskStmt.executeQuery();

            while (resultSet.next()) {
                String imie = resultSet.getString("imie");
                String nazwisko = resultSet.getString("nazwisko");
                String taskName = resultSet.getString("task_name");
                Timestamp startDate = resultSet.getTimestamp("start_date");
                Timestamp completionDate = resultSet.getTimestamp("end_date");
                int statusId = resultSet.getInt("status_id");

                Employee employee = new Employee(imie, nazwisko, null, null, null);
                employee.setSelectedTask(taskName);
                employee.setStatusId(statusId);

                if (startDate != null) {
                    employee.setTimerDisplay(startDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }

                if (completionDate != null) {
                    employee.setCompletionTimeDisplay(completionDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    private void saveStartDateToDatabase(Employee employee) {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String userIdQuery = "SELECT user_id FROM users WHERE imie = ? AND nazwisko = ?";
                PreparedStatement userIdStmt = connectDB.prepareStatement(userIdQuery);
                userIdStmt.setString(1, employee.getImie());
                userIdStmt.setString(2, employee.getNazwisko());
                ResultSet userIdResult = userIdStmt.executeQuery();
                int userId = -1;
                if (userIdResult.next()) {
                    userId = userIdResult.getInt("user_id");
                }

                String taskIdQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
                PreparedStatement taskIdStmt = connectDB.prepareStatement(taskIdQuery);
                taskIdStmt.setString(1, employee.getSelectedTask());
                ResultSet taskIdResult = taskIdStmt.executeQuery();
                int taskId = -1;
                if (taskIdResult.next()) {
                    taskId = taskIdResult.getInt("task_id");
                }

                // Update query to set start_date in user_tasks table
                String updateQuery = "UPDATE user_tasks SET start_date = ? WHERE user_id = ? AND task_id = ?";
                PreparedStatement updateStmt = connectDB.prepareStatement(updateQuery);
                updateStmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, taskId);
                updateStmt.executeUpdate(); // Execute the update statement

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void saveCompletionDateToDatabase(Employee employee) {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            try {
                String userIdQuery = "SELECT user_id FROM users WHERE imie = ? AND nazwisko = ?";
                PreparedStatement userIdStmt = connectDB.prepareStatement(userIdQuery);
                userIdStmt.setString(1, employee.getImie());
                userIdStmt.setString(2, employee.getNazwisko());
                ResultSet userIdResult = userIdStmt.executeQuery();
                int userId = -1;
                if (userIdResult.next()) {
                    userId = userIdResult.getInt("user_id");
                }

                String taskIdQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
                PreparedStatement taskIdStmt = connectDB.prepareStatement(taskIdQuery);
                taskIdStmt.setString(1, employee.getSelectedTask());
                ResultSet taskIdResult = taskIdStmt.executeQuery();
                int taskId = -1;
                if (taskIdResult.next()) {
                    taskId = taskIdResult.getInt("task_id");
                }

                // Update query to set end_date in user_tasks table
                String updateQuery = "UPDATE user_tasks SET end_date = ? WHERE user_id = ? AND task_id = ?";
                PreparedStatement updateStmt = connectDB.prepareStatement(updateQuery);
                updateStmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, taskId);
                updateStmt.executeUpdate(); // Execute the update statement

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    @FXML
    private void handleUtworzButton(ActionEvent event) {
        UserSession userSession = UserSession.getInstance();
        String userRole = userSession.getUserRole();

        if ("admin".equals(userRole) || "kierownik".equals(userRole)) {
            String selectedEmployee = pracownik.getValue();
            String selectedTaskName = choiceBoxZadania.getValue();

            if (selectedEmployee != null && !selectedEmployee.isEmpty() &&
                    selectedTaskName != null && !selectedTaskName.isEmpty()) {

                // Split selectedEmployee to get first name and last name
                String[] employeeData = selectedEmployee.split(" ");
                Employee employee = new Employee(employeeData[0], employeeData[1], null, null, null);
                employee.setSelectedTask(selectedTaskName);

                // Check if the task already exists for the employee
                if (isTaskAlreadyAssigned(employee)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Zadanie już istnieje");
                    alert.setHeaderText("Nie można utworzyć zadania");
                    alert.setContentText("Zadanie o nazwie " + selectedTaskName + " jest już przypisane do pracownika " + selectedEmployee + ".");
                    alert.showAndWait();
                    return; // Exit the method to prevent further processing
                }

                // Clear existing data in employeeList
                employeeList.clear();

                // Add new employee task
                dataManager.addEmployeeTask(employee);

                // Save new employee task to database
                saveEmployeeTaskToDatabase(employee);

                // Refresh table view (automatically updates due to data binding)
                tableView.refresh();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Brak autoryzacji");
            alert.setHeaderText("Nie masz wystarczających uprawnień");
            alert.setContentText("Ta operacja wymaga uprawnień administratora lub kierownika.");
            alert.showAndWait();
        }
    }
    private boolean isTaskAlreadyAssigned(Employee employee) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            String userIdQuery = "SELECT user_id FROM users WHERE imie = ? AND nazwisko = ?";
            PreparedStatement userIdStmt = connectDB.prepareStatement(userIdQuery);
            userIdStmt.setString(1, employee.getImie());
            userIdStmt.setString(2, employee.getNazwisko());
            ResultSet userIdResult = userIdStmt.executeQuery();
            int userId = -1;
            if (userIdResult.next()) {
                userId = userIdResult.getInt("user_id");
            }

            String taskIdQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
            PreparedStatement taskIdStmt = connectDB.prepareStatement(taskIdQuery);
            taskIdStmt.setString(1, employee.getSelectedTask());
            ResultSet taskIdResult = taskIdStmt.executeQuery();
            int taskId = -1;
            if (taskIdResult.next()) {
                taskId = taskIdResult.getInt("task_id");
            }

            // Query to check if the task is already assigned to the employee
            String checkQuery = "SELECT * FROM user_tasks WHERE user_id = ? AND task_id = ?";
            PreparedStatement checkStmt = connectDB.prepareStatement(checkQuery);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, taskId);
            ResultSet checkResult = checkStmt.executeQuery();

            return checkResult.next(); // Return true if the task is already assigned to the employee

        } catch (SQLException e) {
            handleSQLException(e);
            return true; // Handle SQL exception by returning true (for safety)
        }
    }
    private void handleSQLException(SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd SQL");
        alert.setHeaderText("Wystąpił błąd podczas operacji na bazie danych");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }


    private void saveEmployeeTaskToDatabase(Employee employee) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            // Fetch user_id from users table
            String userIdQuery = "SELECT user_id FROM users WHERE imie = ? AND nazwisko = ?";
            PreparedStatement userIdStmt = connectDB.prepareStatement(userIdQuery);
            userIdStmt.setString(1, employee.getImie());
            userIdStmt.setString(2, employee.getNazwisko());
            ResultSet userIdResult = userIdStmt.executeQuery();
            int userId = -1;
            if (userIdResult.next()) {
                userId = userIdResult.getInt("user_id");
            }

            // Fetch task_id from tasks table
            String taskIdQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
            PreparedStatement taskIdStmt = connectDB.prepareStatement(taskIdQuery);
            taskIdStmt.setString(1, employee.getSelectedTask());
            ResultSet taskIdResult = taskIdStmt.executeQuery();
            int taskId = -1;
            if (taskIdResult.next()) {
                taskId = taskIdResult.getInt("task_id");
            }

            // Clear existing data in employeeList
            employeeList.clear();

            // Insert into user_tasks table
            String insertQuery = "INSERT INTO user_tasks (user_id, task_id, status_id) VALUES (?, ?, 1)";
            PreparedStatement insertStmt = connectDB.prepareStatement(insertQuery);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, taskId);
            insertStmt.executeUpdate();

            // Refresh table view after inserting new task
            loadAllEmployeeTasks();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setColumnCellFactory(TableColumn<Employee, String> column) {
        column.setCellFactory(new Callback<TableColumn<Employee, String>, TableCell<Employee, String>>() {
            @Override
            public TableCell<Employee, String> call(TableColumn<Employee, String> param) {
                return new TableCell<Employee, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });
    }

    @FXML
        private void goToMainPage(MouseEvent event) {
            try {
                // Ładowanie widoku głównej strony
                FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) label_sklep.getScene().getWindow();
                stage.setScene(new Scene(root)); // Ustawienie nowej sceny
                stage.show(); // Wyświetlenie nowej sceny
            } catch (IOException e) {
                handleIOException(e); // Obsługa IOException przy ładowaniu widoku
            }
        }

        private void handleIOException(IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wejścia/wyjścia");
            alert.setHeaderText("Wystąpił błąd wejścia/wyjścia");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        public ObservableList<Employee> getEmployeeList() {
            return tableView.getItems();
        }

        public void setUserRole(String userRole) {
            // Method not implemented yet
        }

        public void refreshData() {
            // Method not implemented yet
        }
    }

