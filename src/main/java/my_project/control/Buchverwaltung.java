package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.model.abitur.datenbanken.mysql.QueryResult;

public class Buchverwaltung {

    private final DatabaseController db;

    public Buchverwaltung(DatabaseController db) {
        this.db = db;
    }

    public void buecherAnzeigen() {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        db.executeStatement(
                "SELECT ID, Titel, ISBN, Genre, Standort "
                        + "FROM `26ArDa_buecher` ORDER BY ID;"
        );

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return;
        }

        QueryResult ergebnis = db.getCurrentQueryResult();

        if (ergebnis == null) {
            System.err.println("Kein Abfrageergebnis erhalten.");
            return;
        }

        if (ergebnis.getRowCount() == 0) {
            System.out.println("Noch keine Bücher vorhanden.");
            return;
        }

        String[][] daten = ergebnis.getData();

        for (int zeile = 0; zeile < daten.length; zeile++) {
            for (int spalte = 0; spalte < daten[zeile].length; spalte++) {
                System.out.print(daten[zeile][spalte] + " ");
            }

            System.out.println();
        }
    }

    public void buchAnlegen(String titel, String isbn,
                            String genre, String standort) {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        if (titel == null || titel.trim().isEmpty()) {
            System.out.println("Bitte einen Titel angeben.");
            return;
        }

        if (titel.trim().length() > 150 ||
                (isbn != null && isbn.length() > 13) ||
                (genre != null && genre.length() > 50) ||
                (standort != null && standort.length() > 50)) {
            System.out.println("Eine Angabe ist zu lang.");
            return;
        }

        String sql =
                "INSERT INTO `26ArDa_buecher` "
                        + "(Titel, ISBN, Genre, Standort) VALUES ("
                        + sqlWert(titel.trim()) + ", "
                        + sqlWert(isbn) + ", "
                        + sqlWert(genre) + ", "
                        + sqlWert(standort) + ");";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("Fehler beim Anlegen: " + db.getErrorMessage());
        } else {
            System.out.println("Buch wurde gespeichert.");
        }
    }

    private String sqlWert(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "NULL";
        }

        return "'" + text.replace("\\", "\\\\").replace("'", "''") + "'";
    }

    public void buchSuchen(int id) {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        if (id <= 0) {
            System.out.println("Bitte eine positive Buch-ID angeben.");
            return;
        }

        db.executeStatement(
                "SELECT ID, Titel, ISBN, Genre, Standort "
                        + "FROM `26ArDa_buecher` WHERE ID = " + id + ";"
        );

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return;
        }

        QueryResult ergebnis = db.getCurrentQueryResult();

        if (ergebnis == null) {
            System.err.println("Kein Abfrageergebnis erhalten.");
            return;
        }

        if (ergebnis.getRowCount() == 0) {
            System.out.println("Kein Buch mit der ID " + id + " gefunden.");
            return;
        }

        String[][] daten = ergebnis.getData();

        for (int spalte = 0; spalte < daten[0].length; spalte++) {
            System.out.print(daten[0][spalte] + " ");
        }

        System.out.println();
    }

    public void verfuegbareBuecherAnzeigen() {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        String sql =
                "SELECT b.ID, b.Titel, b.ISBN, b.Genre, b.Standort " +
                        "FROM `26ArDa_buecher` b " +
                        "WHERE NOT EXISTS (" +
                        "SELECT a.ID FROM `26ArDa_ausleihen` a " +
                        "WHERE a.BuchID = b.ID " +
                        "AND a.Rueckgabedatum IS NULL" +
                        ") ORDER BY b.ID;";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return;
        }

        QueryResult ergebnis = db.getCurrentQueryResult();

        if (ergebnis == null) {
            System.err.println("Kein Abfrageergebnis erhalten.");
            return;
        }

        if (ergebnis.getRowCount() == 0) {
            System.out.println("Keine Bücher verfügbar.");
            return;
        }

        String[][] daten = ergebnis.getData();

        for (int i = 0; i < daten.length; i++) {
            System.out.println(
                    "Buch-ID: " + daten[i][0] +
                            " | Titel: " + daten[i][1] +
                            " | ISBN: " + daten[i][2] +
                            " | Genre: " + daten[i][3] +
                            " | Standort: " + daten[i][4]
            );
        }
    }


    public void buecherNachTitelSuchen(String suchtext) {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        if (suchtext == null || suchtext.trim().isEmpty()) {
            System.out.println("Bitte einen Suchtext eingeben.");
            return;
        }

        String muster = suchtext.trim()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");

        String sql =
                "SELECT ID, Titel, ISBN, Genre, Standort " +
                        "FROM `26ArDa_buecher` " +
                        "WHERE Titel LIKE " + sqlWert("%" + muster + "%") +
                        " ESCAPE '!' ORDER BY Titel, ID;";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return;
        }

        QueryResult ergebnis = db.getCurrentQueryResult();

        if (ergebnis == null) {
            System.err.println("Kein Abfrageergebnis erhalten.");
            return;
        }

        if (ergebnis.getRowCount() == 0) {
            System.out.println("Keine passenden Bücher gefunden.");
            return;
        }

        String[][] daten = ergebnis.getData();

        for (int i = 0; i < daten.length; i++) {
            System.out.println(
                    "Buch-ID: " + daten[i][0] +
                            " | Titel: " + daten[i][1] +
                            " | ISBN: " + daten[i][2] +
                            " | Genre: " + daten[i][3] +
                            " | Standort: " + daten[i][4]
            );
        }
    }

}