package com.example.sklep;

public class SummaryEntry {
    private String imie;
    private String nazwisko;
    private String zadanie;
    private long czas; // Czas w minutach

    public SummaryEntry(String imie, String nazwisko, String zadanie, long czas) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.zadanie = zadanie;
        this.czas = czas;
    }

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

    public long getCzas() {
        return czas;
    }

    public void setCzas(long czas) {
        this.czas = czas;
    }
}
