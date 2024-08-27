package com.example.sklep;

public class Task {
    private String taskName;
    private int totalTimeInSeconds;

    public Task(String taskName, int totalTimeInSeconds) {
        this.taskName = taskName;
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTotalSeconds() {
        return totalTimeInSeconds;
    }

    public void setTotalTimeInSeconds(int totalTimeInSeconds) {
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    // Method to format total time in hh:mm:ss format
    public String formatTotalTime() {
        int hours = totalTimeInSeconds / 3600;
        int minutes = (totalTimeInSeconds % 3600) / 60;
        int seconds = totalTimeInSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
