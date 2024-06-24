package com.example.sklep;

public class Task {
    private String taskName;
    private int totalSeconds;

    public Task(String taskName, int totalSeconds) {
        this.taskName = taskName;
        this.totalSeconds = totalSeconds;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}