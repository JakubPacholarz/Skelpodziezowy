package com.example.sklep;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;

public class Employee {
    private Date startDate;
    private Date completionDate; // Declare completionDate
    private StringProperty imie;
    private StringProperty nazwisko;
    private StringProperty email;
    private StringProperty password;
    private StringProperty rola;
    private boolean taskCompleted;
    private StringProperty selectedTask;
    private StringProperty status;
    private StringProperty creationDate;
    private int userId;
    private int id;
    private int taskId;
    private StringProperty timerDisplay = new SimpleStringProperty("");
    private String completionTimeDisplay;
    private int statusId;

    private ObservableList<Task> selectedTasks = FXCollections.observableArrayList();

    public Employee(String imie, String nazwisko, String email, String password, String rola) {
        this.imie = new SimpleStringProperty(imie);
        this.nazwisko = new SimpleStringProperty(nazwisko);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.rola = new SimpleStringProperty(rola);
        this.timerDisplay = new SimpleStringProperty("");
        this.taskCompleted = false;
        this.selectedTask = new SimpleStringProperty("");
        this.status = new SimpleStringProperty("");
        this.creationDate = new SimpleStringProperty("");
        this.startDate = null; // Initially set to null
        this.completionTimeDisplay = ""; // Initialize directly
        this.completionDate = null; // Initially set to null
        this.statusId = statusId;
    }

    // Getter and setter for startDate
    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    // Add getter and setter for completionDate
    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getCompletionTimeDisplay() {
        return completionTimeDisplay;
    }

    public void setCompletionTimeDisplay(String completionTimeDisplay) {
        this.completionTimeDisplay = completionTimeDisplay;
    }

    public StringProperty timerDisplayProperty() {
        return timerDisplay;
    }

    public void setTimerDisplay(String timerDisplay) {
        this.timerDisplay.set(timerDisplay);
    }

    // Update setStartDate method to use current date
    public void setStartDate() {
        this.startDate = startDate;}

    // Getters for other properties
    public String getImie() {
        return imie.get();
    }

    public StringProperty imieProperty() {
        return imie;
    }

    public String getNazwisko() {
        return nazwisko.get();
    }

    public StringProperty nazwiskoProperty() {
        return nazwisko;
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getRola() {
        return rola.get();
    }

    public StringProperty rolaProperty() {
        return rola;
    }

    public String getCreationDate() {
        return creationDate.get();
    }

    public StringProperty creationDateProperty() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate.set(creationDate);
    }

    public String getTimerDisplay() {
        return timerDisplay.get();
    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public ObservableList<Task> getSelectedTasks() {
        return selectedTasks;
    }

    public void setSelectedTasks(ObservableList<Task> selectedTasks) {
        this.selectedTasks = selectedTasks;
    }

    public String getSelectedTask() {
        return selectedTask.get();
    }

    public StringProperty selectedTaskProperty() {
        return selectedTask;
    }

    public void setSelectedTask(String selectedTask) {
        this.selectedTask.set(selectedTask);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    // Method to save start date to database
    /*public void saveStartDateToDatabase() {
        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection()) {
            String userIdQuery = "SELECT user_id FROM users WHERE imie = ? AND nazwisko = ?";
            try (PreparedStatement userIdStmt = connectDB.prepareStatement(userIdQuery)) {
                userIdStmt.setString(1, getImie());
                userIdStmt.setString(2, getNazwisko());
                try (ResultSet userIdResult = userIdStmt.executeQuery()) {
                    if (userIdResult.next()) {
                        int userId = userIdResult.getInt("user_id");
                        String taskIdQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
                        try (PreparedStatement taskIdStmt = connectDB.prepareStatement(taskIdQuery)) {
                            taskIdStmt.setString(1, getSelectedTask());
                            try (ResultSet taskIdResult = taskIdStmt.executeQuery()) {
                                if (taskIdResult.next()) {
                                    int taskId = taskIdResult.getInt("task_id");
                                    String insertQuery = "UPDATE user_tasks SET start_date = ?, completion_date = ? WHERE user_id = ? AND task_id = ?";
                                    try (PreparedStatement insertStmt = connectDB.prepareStatement(insertQuery)) {
                                        insertStmt.setDate(1, java.sql.Date.valueOf(startDate));
                                        insertStmt.setDate(2, completionDate != null ? java.sql.Date.valueOf(completionDate) : null);
                                        insertStmt.setInt(3, userId);
                                        insertStmt.setInt(4, taskId);
                                        insertStmt.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    // Method to update total time spent on tasks in database
    private void updateTotalTimeSpentInDatabase(int totalTimeInSeconds) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE users SET total_time_spent = ? WHERE id = ?";
            try (PreparedStatement updateStmt = connectDB.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, totalTimeInSeconds);
                updateStmt.setInt(2, getId());
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch total time spent from database
    public int fetchTotalTimeSpentFromDatabase() {
        int totalTimeSpent = 0;
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String selectQuery = "SELECT total_time_spent FROM users WHERE id = ?";
            try (PreparedStatement selectStatement = connectDB.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, getId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        totalTimeSpent = resultSet.getInt("total_time_spent");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalTimeSpent;
    }

    // Method to show task completed alert
    private void showTaskCompletedAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Zadanie Ukończone");
        alert.setHeaderText(null);
        alert.setContentText("Zadanie dla " + getImie() + " " + getNazwisko() + " zostało zakończone.");
        alert.showAndWait();
    }

    // Method to add completed task to user_tasks table
    private void addToUserTasksInDatabase(Task task) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            int userId = getUserId();
            int taskId = fetchTaskIdFromDatabase(task, connectDB);
            if (taskId != -1) {
                String checkEntryQuery = "SELECT * FROM user_tasks WHERE user_id = ? AND task_id = ?";
                try (PreparedStatement checkEntryStmt = connectDB.prepareStatement(checkEntryQuery)) {
                    checkEntryStmt.setInt(1, userId);
                    checkEntryStmt.setInt(2, taskId);
                    try (ResultSet checkEntryResult = checkEntryStmt.executeQuery()) {
                        if (!checkEntryResult.next()) {
                            String insertQuery = "INSERT INTO user_tasks (user_id, task_id) VALUES (?, ?)";
                            try (PreparedStatement insertStmt = connectDB.prepareStatement(insertQuery)) {
                                insertStmt.setInt(1, userId);
                                insertStmt.setInt(2, taskId);
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch task_id from tasks table or insert if does not exist
    private int fetchTaskIdFromDatabase(Task task, Connection connectDB) {
        int taskId = -1;
        try {
            String checkTaskQuery = "SELECT task_id FROM tasks WHERE task_name = ?";
            try (PreparedStatement checkTaskStmt = connectDB.prepareStatement(checkTaskQuery)) {
                checkTaskStmt.setString(1, task.getTaskName());
                try (ResultSet checkTaskResult = checkTaskStmt.executeQuery()) {
                    if (checkTaskResult.next()) {
                        taskId = checkTaskResult.getInt("task_id");
                    } else {
                        String insertTaskQuery = "INSERT INTO tasks (task_name) VALUES (?)";
                        try (PreparedStatement insertTaskStmt = connectDB.prepareStatement(insertTaskQuery, Statement.RETURN_GENERATED_KEYS)) {
                            insertTaskStmt.setString(1, task.getTaskName());
                            insertTaskStmt.executeUpdate();
                            ResultSet generatedKeys = insertTaskStmt.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                taskId = generatedKeys.getInt(1);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskId;
    }

    // Method to fetch selected tasks of the employee from database
    public ObservableList<Task> fetchSelectedTasksFromDatabase() {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            int userId = getUserId();
            String tasksQuery = "SELECT t.task_name, ut.total_time_seconds " +
                    "FROM tasks t " +
                    "JOIN user_tasks ut ON t.task_id = ut.task_id " +
                    "WHERE ut.user_id = ?";
            try (PreparedStatement tasksStmt = connectDB.prepareStatement(tasksQuery)) {
                tasksStmt.setInt(1, userId);
                try (ResultSet tasksResult = tasksStmt.executeQuery()) {
                    while (tasksResult.next()) {
                        String taskName = tasksResult.getString("task_name");
                        int totalTimeInSeconds = tasksResult.getInt("total_time_seconds");
                        Task task = new Task(taskName, totalTimeInSeconds);
                        tasks.add(task);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // Getter and setter for userId
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter and setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Method to add employee to the database
    public void addToDatabase(Connection connection) throws SQLException {
        String insertQuery = "INSERT INTO users (imie, nazwisko, email, password, rola) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, getImie());
            insertStatement.setString(2, getNazwisko());
            insertStatement.setString(3, getEmail());
            insertStatement.setString(4, getPassword());
            insertStatement.setString(5, getRola());
            insertStatement.executeUpdate();
        }
    }

    // Method to delete employee from the database
    public void deleteFromDatabase(Connection connectDB) throws SQLException {
        String deleteQuery = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = connectDB.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
        }
    }

    // Method to update total time spent on tasks in users table
    public void setTotalTimeInSeconds(int totalTimeInSeconds) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE users SET total_time_spent = ? WHERE id = ?";
            try (PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, totalTimeInSeconds);
                updateStatement.setInt(2, getId());
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
