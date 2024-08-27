package com.example.sklep;

public class UserSession {
    private static UserSession instance;
    private int loggedInUserId;
    private String loggedInUsername;
    private String loggedInUserLastName;
    private String loggedInUserEmail;
    private String userRole;
    private String selectedTask; // Nowe pole: aktualnie wybrane zadanie
    private String taskStatus;   // Nowe pole: status aktualnie wybranego zadania
    private Task lastSelectedTask;

    // Private constructor to prevent instantiation outside this class
    private UserSession() {
        // Initialize default values
        loggedInUserId = -1; // Default value indicating no user logged in
        loggedInUsername = "";
        loggedInUserLastName = "";
        loggedInUserEmail = "";
        userRole = "";
        selectedTask = "";
        taskStatus = "";
    }

    // Singleton pattern: Returns the single instance of UserSession
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public void setLoggedInUsername(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
    }

    public String getLoggedInUserLastName() {
        return loggedInUserLastName;
    }

    public void setLoggedInUserLastName(String loggedInUserLastName) {
        this.loggedInUserLastName = loggedInUserLastName;
    }

    public String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }

    public void setLoggedInUserEmail(String loggedInUserEmail) {
        this.loggedInUserEmail = loggedInUserEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getSelectedTask() {
        return selectedTask;
    }

    public void setSelectedTask(String selectedTask) {
        this.selectedTask = selectedTask;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    // Method to clear user session data when user logs out
    public void cleanUserSession() {
        loggedInUserId = -1; // Reset to default value
        loggedInUsername = "";
        loggedInUserLastName = "";
        loggedInUserEmail = "";
        userRole = "";
        cleanTaskSession(); // Clean task session data as well
    }

    // Method to clear task session data
    public void cleanTaskSession() {
        selectedTask = "";
        taskStatus = "";
    }

    // Optional: Method to check if a user is logged in
    public boolean isLoggedIn() {
        return loggedInUserId != -1;
    }
    public Task getLastSelectedTask() {
        return lastSelectedTask;
    }

    public void setLastSelectedTask(Task lastSelectedTask) {
        this.lastSelectedTask = lastSelectedTask;
    }
}
