package com.example.sklep;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

public class Employee {
    private StringProperty imie;
    private StringProperty nazwisko;
    private StringProperty timerDisplay;
    private StringProperty buttonLabel; // Tekst przycisku
    private boolean timerRunning;
    private Timeline timer;
    private int hours;
    private int minutes;
    private int seconds;
    private boolean taskCompleted;
    private StringProperty selectedTask; // Wybrane zadanie przez pracownika

    public Employee(String imie, String nazwisko) {
        this.imie = new SimpleStringProperty(imie);
        this.nazwisko = new SimpleStringProperty(nazwisko);
        this.timerDisplay = new SimpleStringProperty("00:00:00");
        this.buttonLabel = new SimpleStringProperty("Start"); // Domyślnie ustawiamy przycisk na "Start"
        this.timerRunning = false;
        this.taskCompleted = false;
        this.selectedTask = new SimpleStringProperty(""); // Inicjalizujemy jako pusty String
    }

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

    public String getTimerDisplay() {
        return timerDisplay.get();
    }

    public StringProperty timerDisplayProperty() {
        return timerDisplay;
    }

    public String getButtonLabel() {
        return buttonLabel.get();
    }

    public StringProperty buttonLabelProperty() {
        return buttonLabel;
    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }

    public void setSelectedTask(String task) {
        selectedTask.set(task);
    }

    public String getSelectedTask() {
        return selectedTask.get();
    }

    public StringProperty selectedTaskProperty() {
        return selectedTask;
    }

    public void startOrStopTimer() {
        if (!timerRunning) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
                if (minutes == 60) {
                    minutes = 0;
                    hours++;
                }
            }
            updateTimerDisplay();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        timerRunning = true;
        buttonLabel.set("Stop");
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timerRunning = false;
            taskCompleted = true;
            showTaskCompletedAlert();
            buttonLabel.set("Ukończone");
        }
    }

    private void updateTimerDisplay() {
        String hoursStr = String.format("%02d", hours);
        String minutesStr = String.format("%02d", minutes);
        String secondsStr = String.format("%02d", seconds);
        timerDisplay.set(hoursStr + ":" + minutesStr + ":" + secondsStr);
    }

    private void showTaskCompletedAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Zadanie zakończone");
        alert.setHeaderText(null);
        alert.setContentText("Zadanie dla " + getImie() + " " + getNazwisko() + " zakończone.");
        alert.showAndWait();
    }
}
