package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.model.abitur.datenbanken.mysql.QueryResult;

public class Buchverwaltung {

    private final DatabaseController db;

    public Buchverwaltung(DatabaseController db) {
        this.db = db;
    }

    public void buecherAnzeigen() {
        QueryResult ergebnis = abfrageAusfuehren(
                "SELECT ID, Titel, ISBN, Genre, Standort " +
                        "FROM `26ArDa_buecher` ORDER BY ID;"
        );

        ergebnisAnzeigen(ergebnis, "Noch keine Bücher vorhanden.");
    }

    public String[][] buecherDatenHolen() {

        QueryResult ergebnis = abfrageAusfuehren(
                "SELECT ID, Titel, ISBN, Genre, Standort " +
                        "FROM `26ArDa_buecher` ORDER BY ID;"
        );

        if (ergebnis == null) {
            return new String[0][0];
        }

        return ergebnis.getData();
    }

    public void buchAnlegen(String titel, String isbn,
                            String genre, String standort) {
        if (!verbindungPruefen()) {
            return;
        }

        if (!buchdatenPruefen(titel, isbn, genre, standort)) {
            return;
        }

        String sql =
                "INSERT INTO `26ArDa_buecher` " +
                        "(Titel, ISBN, Genre, Standort) VALUES (" +
                        sqlWert(titel.trim()) + ", " +
                        sqlWert(isbn) + ", " +
                        sqlWert(genre) + ", " +
                        sqlWert(standort) + ");";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("Fehler beim Anlegen: " + db.getErrorMessage());
        } else {
            System.out.println("Buch wurde gespeichert.");
        }
    }

    public void buchBearbeiten(int id, String titel, String isbn,
                               String genre, String standort) {
        if (!verbindungPruefen()) {
            return;
        }

        if (id <= 0) {
            System.out.println("Bitte eine positive Buch-ID angeben.");
            return;
        }

        if (!buchdatenPruefen(titel, isbn, genre, standort)) {
            return;
        }

        QueryResult ergebnis = abfrageAusfuehren(
                "SELECT ID FROM `26ArDa_buecher` WHERE ID = " + id + ";"
        );

        if (ergebnis == null) {
            return;
        }

        if (ergebnis.getRowCount() == 0) {
            System.out.println("Kein Buch mit der ID " + id + " gefunden.");
            return;
        }

        String sql =
                "UPDATE `26ArDa_buecher` SET " +
                        "Titel = " + sqlWert(titel.trim()) + ", " +
                        "ISBN = " + sqlWert(isbn) + ", " +
                        "Genre = " + sqlWert(genre) + ", " +
                        "Standort = " + sqlWert(standort) + " " +
                        "WHERE ID = " + id + ";";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println(
                    "Fehler beim Bearbeiten: " + db.getErrorMessage()
            );
        } else {
            System.out.println("Buchdaten wurden gespeichert.");
        }
    }

    public void buchLoeschen(int id) {
        if (!verbindungPruefen()) {
            return;
        }

        if (id <= 0) {
            System.out.println("Bitte eine positive Buch-ID angeben.");
            return;
        }

        QueryResult buch = abfrageAusfuehren(
                "SELECT ID FROM `26ArDa_buecher` WHERE ID = " + id + ";"
        );

        if (buch == null) {
            return;
        }

        if (buch.getRowCount() == 0) {
            System.out.println("Kein Buch mit der ID " + id + " gefunden.");
            return;
        }

        QueryResult ausleihen = abfrageAusfuehren(
                "SELECT ID FROM `26ArDa_ausleihen` " +
                        "WHERE BuchID = " + id + " LIMIT 1;"
        );

        if (ausleihen == null) {
            return;
        }

        if (ausleihen.getRowCount() > 0) {
            System.out.println(
                    "Das Buch kann nicht gelöscht werden, " +
                            "weil Ausleihen darauf verweisen."
            );
            return;
        }

        db.executeStatement(
                "DELETE FROM `26ArDa_buecher` WHERE ID = " + id + ";"
        );

        if (db.getErrorMessage() != null) {
            System.err.println("Fehler beim Löschen: " + db.getErrorMessage());
        } else {
            System.out.println("Buch wurde gelöscht.");
        }
    }

    public void buecherSuchen(String suchtext) {
        if (suchtext == null || suchtext.trim().isEmpty()) {
            System.out.println("Bitte einen Suchtext eingeben.");
            return;
        }

        String sql =
                "SELECT ID, Titel, ISBN, Genre, Standort " +
                        "FROM `26ArDa_buecher` " +
                        "WHERE Titel LIKE '%" + suchtext.trim() + "%' " +
                        "OR ID = '" + suchtext.trim() + "' " +
                        "OR ISBN LIKE '%" + suchtext.trim() + "%';";

        QueryResult ergebnis = abfrageAusfuehren(sql);

        ergebnisAnzeigen(ergebnis, "Keine passenden Bücher gefunden.");
    }

    public void verfuegbareBuecherAnzeigen() {
        String sql =
                "SELECT b.ID, b.Titel, b.ISBN, b.Genre, b.Standort " +
                        "FROM `26ArDa_buecher` b " +
                        "WHERE NOT EXISTS (" +
                        "SELECT a.ID FROM `26ArDa_ausleihen` a " +
                        "WHERE a.BuchID = b.ID " +
                        "AND a.Rueckgabedatum IS NULL" +
                        ") ORDER BY b.ID;";

        QueryResult ergebnis = abfrageAusfuehren(sql);

        ergebnisAnzeigen(ergebnis, "Keine Bücher verfügbar.");
    }

    public void buecherNachTitelSuchen(String suchtext) {
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

        QueryResult ergebnis = abfrageAusfuehren(sql);

        ergebnisAnzeigen(ergebnis, "Keine passenden Bücher gefunden.");
    }

    private boolean verbindungPruefen() {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return false;
        }

        return true;
    }

    private boolean buchdatenPruefen(String titel, String isbn,
                                     String genre, String standort) {
        if (titel == null || titel.trim().isEmpty()) {
            System.out.println("Bitte einen Titel angeben.");
            return false;
        }

        if (titel.trim().length() > 150) {
            System.out.println("Der Titel darf höchstens 150 Zeichen haben.");
            return false;
        }

        if (isbn != null && isbn.trim().length() > 13) {
            System.out.println("Die ISBN darf höchstens 13 Zeichen haben.");
            return false;
        }

        if (genre != null && genre.trim().length() > 50) {
            System.out.println("Das Genre darf höchstens 50 Zeichen haben.");
            return false;
        }

        if (standort != null && standort.trim().length() > 50) {
            System.out.println("Der Standort darf höchstens 50 Zeichen haben.");
            return false;
        }

        return true;
    }

    private QueryResult abfrageAusfuehren(String sql) {
        if (!verbindungPruefen()) {
            return null;
        }

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return null;
        }

        QueryResult ergebnis = db.getCurrentQueryResult();

        if (ergebnis == null) {
            System.err.println("Kein Abfrageergebnis erhalten.");
        }

        return ergebnis;
    }

    private void ergebnisAnzeigen(QueryResult ergebnis, String leerMeldung) {
        if (ergebnis == null) {
            return;
        }

        if (ergebnis.getRowCount() == 0) {
            System.out.println(leerMeldung);
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

    private String sqlWert(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "NULL";
        }

        return "'" + text.trim()
                .replace("\\", "\\\\")
                .replace("'", "''") + "'";
    }
}