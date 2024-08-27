package com.example.sklep;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;

public class SummaryEntry {
    private String imie;
    private String nazwisko;
    private String zadanie;
    private long hours;
    private long minutes;
    private long seconds;
    private long totalSeconds;

    // Constructor, getters, and setters
    public SummaryEntry(String imie, String nazwisko, String zadanie, Duration duration) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.zadanie = zadanie;

        // Calculate hours, minutes, seconds from duration
        this.hours = duration.toHours();
        this.minutes = duration.toMinutesPart();
        this.seconds = duration.toSecondsPart();

        // Calculate total seconds for displaying
        this.totalSeconds = duration.toSeconds();
    }

    // Getters and setters
    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getZadanie() {
        return zadanie;
    }

    public void setZadanie(String zadanie) {
        this.zadanie = zadanie;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(long totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    // Method to format duration as hh:mm:ss
    public String getFormattedDuration() {
        long tempHours = totalSeconds / 3600;
        long tempMinutes = (totalSeconds % 3600) / 60;
        long tempSeconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", tempHours, tempMinutes, tempSeconds);
    }
}
