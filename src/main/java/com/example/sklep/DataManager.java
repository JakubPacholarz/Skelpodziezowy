package com.example.sklep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static DataManager instance = new DataManager();

    private ObservableList<Employee> employeeTasks = FXCollections.observableArrayList();
    private Map<String, List<Task>> employeeTasksMap = new HashMap<>();

    private DataManager() {
        // Private constructor to prevent instantiation
    }

    public static DataManager getInstance() {
        return instance;
    }

    public ObservableList<Employee> getEmployeeTasks() {
        return employeeTasks;
    }

    public void addEmployeeTask(Employee employee) {
        employeeTasks.add(employee);
        updateEmployeeTasksMap(employee);
    }

    // Optional: Method to update employee time in DataManager
    public void updateEmployeeTime(Employee employee, int totalTimeSeconds) {
        // Update the time for the specified employee
        for (Employee emp : employeeTasks) {
            if (emp.equals(employee)) {
                emp.setTotalTimeInSeconds(totalTimeSeconds);
                break;
            }
        }
        // Optionally, update employeeTasksMap for efficient retrieval
        updateEmployeeTasksMap(employee);
    }

    private void updateEmployeeTasksMap(Employee employee) {
        String key = employee.getImie() + " " + employee.getNazwisko();
        ObservableList<Task> tasks = employee.getSelectedTasks();
        employeeTasksMap.put(key, tasks);
    }

    // Optional: Method to retrieve tasks for a specific employee from employeeTasksMap
    public List<Task> getTasksForEmployee(String employeeName) {
        return employeeTasksMap.getOrDefault(employeeName, List.of());
    }

    // Optional: Method to clear employee tasks from DataManager
    public void clearEmployeeTasks() {
        employeeTasks.clear();
        employeeTasksMap.clear();
    }
}
