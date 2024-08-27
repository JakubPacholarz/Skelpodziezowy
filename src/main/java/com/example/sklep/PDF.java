package com.example.sklep;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.sql.*;

public class PDF {
    DatabaseConnection connectNow = new DatabaseConnection();
    Connection connection = connectNow.getConnection();

    /**
     * Metoda generująca raport PDF na podstawie wybranego typu raportu.
     *
     * @param dest         Ścieżka do pliku docelowego, gdzie zostanie zapisany raport PDF.
     * @param reportType   Typ raportu: "Magazyn", "Pracownicy", "Użytkownicy".
     * @param filterType   Typ filtru do zastosowania (opcjonalny).
     * @param filterPhrase Frazy filtru (opcjonalny).
     * @throws Exception W przypadku błędów podczas generowania raportu.
     */
    public void manipulatePdf(String dest, String reportType, Integer filterType, String filterPhrase) throws Exception {
        // Inicjalizacja pisarza PDF i dokumentu
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        // Wybór odpowiedniej metody generującej raport na podstawie typu raportu
        switch (reportType) {
            case "Magazyn":
                generateMagazynReport(doc, filterType, filterPhrase);
                break;
            case "Zadania":
                generateTaskReport(doc, filterType, filterPhrase);
                break;
            case "Użytkownicy":
                generateUserReport(doc, filterType, filterPhrase);
                break;
            default:
                throw new IllegalArgumentException("Nieobsługiwany typ raportu: " + reportType);
        }

        doc.close(); // Zamknięcie dokumentu PDF po zakończeniu operacji
    }

    /**
     * Metoda generująca raport użytkowników na podstawie filtrów.
     *
     * @param doc         Dokument PDF, do którego dodawane są elementy.
     * @param filterType  Typ filtru do zastosowania (opcjonalny).
     * @param filterPhrase Frazy filtru (opcjonalny).
     * @throws Exception W przypadku błędów podczas generowania raportu użytkowników.
     */
    private void generateUserReport(Document doc, Integer filterType, String filterPhrase) throws Exception {
        String query = "SELECT user_id, imie, nazwisko, email, rola FROM users";

        // Dodanie warunku WHERE w zależności od typu filtru
        if (filterType != null && filterPhrase != null && !filterPhrase.isEmpty()) {
            switch (filterType) {
                case 1:
                    // Brak dodatkowego filtru
                    break;
                case 4:
                    query += " WHERE rola LIKE ?";
                    break; // Filtr po roli
                case 2:
                    query += " WHERE imie LIKE ?";
                    break; // Filtr po imieniu
                case 3:
                    query += " WHERE nazwisko LIKE ?";
                    break; // Filtr po nazwisku
                default:
                    throw new IllegalArgumentException("Nieprawidłowy typ filtru.");
            }
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Ustawienie parametru w PreparedStatement, jeśli filtr jest wymagany
            if (filterType != null && filterPhrase != null && !filterPhrase.isEmpty()) {
                String filterValue = "%" + filterPhrase + "%";
                preparedStatement.setString(1, filterValue);
            }

            ResultSet resultSet = preparedStatement.executeQuery(); // Wykonanie zapytania SQL
            Table table = new Table(UnitValue.createPercentArray(new float[]{10, 20, 30, 20, 20})); // Tabela do przechowywania wyników

            Style headerStyle = new Style()
                    .setFontSize(16)
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);

            Style cellStyle = new Style()
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);

            // Nagłówek tabeli
            Paragraph header = new Paragraph("Tabela Użytkowników").addStyle(headerStyle);
            doc.add(header);

            // Dodanie nagłówków kolumn do tabeli
            table.addHeaderCell(createCell("L.P", headerStyle));
            table.addHeaderCell(createCell("Imie", headerStyle));
            table.addHeaderCell(createCell("Nazwisko", headerStyle));
            table.addHeaderCell(createCell("Email", headerStyle));
            table.addHeaderCell(createCell("Rola", headerStyle));

            int index = 1;
            while (resultSet.next()) {
                // Wypełnienie tabeli wynikami z bazy danych
                table.addCell(createCell(String.valueOf(index++), cellStyle)); // L.P
                table.addCell(createCell(resultSet.getString("imie"), cellStyle));
                table.addCell(createCell(resultSet.getString("nazwisko"), cellStyle));
                table.addCell(createCell(resultSet.getString("email"), cellStyle));
                table.addCell(createCell(resultSet.getString("rola"), cellStyle));
            }
            doc.add(table); // Dodanie tabeli do dokumentu PDF
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd podczas generowania raportu użytkowników: " + e.getMessage());
        }
    }



    /**
     * Metoda generująca raport pracowników na podstawie filtrów.
     *
     * @param doc         Dokument PDF, do którego dodawane są elementy.
     * @param filterType  Typ filtru do zastosowania (opcjonalny).
     * @param filterPhrase Frazy filtru (opcjonalny).
     * @throws Exception W przypadku błędów podczas generowania raportu pracowników.
     */
    private void generateTaskReport(Document doc, Integer filterType, String filterPhrase) throws Exception {
        String query = "SELECT task_id, task_name FROM tasks";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Ustawienie parametru w PreparedStatement, jeśli filtr jest wymagany
            if (filterType != null && filterPhrase != null && !filterPhrase.isEmpty() && (filterType == 2 || filterType == 3)) {
                preparedStatement.setString(1, filterPhrase);
            }

            ResultSet resultSet = preparedStatement.executeQuery(); // Wykonanie zapytania SQL
            Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50})); // Tabela do przechowywania wyników

            Style headerStyle = new Style()
                    .setFontSize(12)
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);

            Style cellStyle = new Style()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);

            // Nagłówek tabeli
            Paragraph header = new Paragraph("Tabela Zadan")
                    .addStyle(headerStyle);
            doc.add(header);

            // Dodanie nagłówków kolumn do tabeli
            table.addHeaderCell(createCell("L.P", headerStyle));
            table.addHeaderCell(createCell("Nazwa Zadania", headerStyle));

            int index = 1;
            while (resultSet.next()) {
                // Wypełnienie tabeli wynikami z bazy danych
                table.addCell(createCell(String.valueOf(index++), cellStyle)); // L.P
                table.addCell(createCell(resultSet.getString("task_name"), cellStyle));
            }
            doc.add(table); // Dodanie tabeli do dokumentu PDF
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd podczas generowania raportu pracowników: " + e.getMessage());
        }
    }


    /**
     * Metoda generująca raport magazynowy na podstawie filtrów.
     *
     * @param doc         Dokument PDF, do którego dodawane są elementy.
     * @param filterType  Typ filtru do zastosowania (opcjonalny).
     * @param filterPhrase Frazy filtru (opcjonalny).
     * @throws Exception W przypadku błędów podczas generowania raportu magazynowego.
     */
    private void generateMagazynReport(Document doc, Integer filterType, String filterPhrase) throws Exception {
        String query = "SELECT dostawca, zasoby, kontakt FROM magazyn";

        // Dodanie warunku WHERE w zależności od typu filtru
        if (filterType != null && filterPhrase != null && !filterPhrase.isEmpty()) {
            query += " WHERE ";
            switch (filterType) {
                case 1:
                    break; // Brak dodatkowego filtru
                case 2:
                    query += "zasoby <= " + filterPhrase;
                    break; // Filtr po ilości zasobów
                case 3:
                    query += "dostawca LIKE '" + filterPhrase + "'";
                    break; // Filtr po nazwie dostawcy
                default:
                    throw new IllegalArgumentException("Nieobsługiwany typ filtru dla raportu: Magazyn.");
            }
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery(); // Wykonanie zapytania SQL
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 20, 30})); // Tabela do przechowywania wyników

            Style headerStyle = new Style()
                    .setFontSize(12)
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);

            Style cellStyle = new Style()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);

            // Nagłówek tabeli
            Paragraph header = new Paragraph("Raport Magazynowy")
                    .addStyle(headerStyle);
            doc.add(header);

            // Dodanie nagłówków kolumn do tabeli
            table.addHeaderCell(createCell("Dostawca", headerStyle));
            table.addHeaderCell(createCell("Zasoby", headerStyle));
            table.addHeaderCell(createCell("Kontakt", headerStyle));

            while (resultSet.next()) {
                // Wypełnienie tabeli wynikami z bazy danych
                table.addCell(createCell(resultSet.getString("dostawca"), cellStyle));
                table.addCell(createCell(resultSet.getString("zasoby"), cellStyle));
                table.addCell(createCell(resultSet.getString("kontakt"), cellStyle));
            }
            doc.add(table); // Dodanie tabeli do dokumentu PDF
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd podczas generowania raportu magazynowego: " + e.getMessage());
        }
    }

    /**
     * Tworzy komórkę tabeli z określonym stylem.
     *
     * @param content Zawartość komórki.
     * @param style   Styl komórki.
     * @return Komórka tabeli.
     */
    private Cell createCell(String content, Style style) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (style != null) {
            cell.addStyle(style);
        }
        return cell;
    }
}

