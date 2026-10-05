package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.model.abitur.datenbanken.mysql.QueryResult;

public class Ausleihverwaltung {

    private final DatabaseController db;

    public Ausleihverwaltung(DatabaseController db) {
        this.db = db;
    }

    public void ausleihenAnzeigen() {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        String sql =
                "SELECT a.ID, m.Vorname, m.Nachname, b.Titel, " +
                        "a.Ausleihdatum, a.FaelligAm, a.Rueckgabedatum " +
                        "FROM `26ArDa_ausleihen` a " +
                        "JOIN `26ArDa_mitglieder` m ON a.MitgliedID = m.ID " +
                        "JOIN `26ArDa_buecher` b ON a.BuchID = b.ID " +
                        "ORDER BY a.ID;";

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
            System.out.println("Noch keine Ausleihen vorhanden.");
            return;
        }

        String[][] daten = ergebnis.getData();

        for (int i = 0; i < daten.length; i++) {
            String rueckgabe = daten[i][6];

            if (rueckgabe == null) {
                rueckgabe = "noch nicht zurückgegeben";
            }

            System.out.println(
                    "Ausleihe " + daten[i][0] +
                            " | Mitglied: " + daten[i][1] + " " + daten[i][2] +
                            " | Buch: " + daten[i][3] +
                            " | Ausgeliehen: " + daten[i][4] +
                            " | Fällig: " + daten[i][5] +
                            " | Rückgabe: " + rueckgabe
            );
        }
    }

    public void buchAusleihen(int mitgliedID, int buchID) {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        if (mitgliedID <= 0 || buchID <= 0) {
            System.out.println("Bitte positive IDs angeben.");
            return;
        }

        db.executeStatement(
                "SELECT ID FROM `26ArDa_mitglieder` WHERE ID = "
                        + mitgliedID + ";"
        );

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return;
        }

        QueryResult mitglied = db.getCurrentQueryResult();

        if (mitglied == null) {
            System.err.println("Kein Abfrageergebnis erhalten.");
            return;
        }

        if (mitglied.getRowCount() == 0) {
            System.out.println("Mitglied wurde nicht gefunden.");
            return;
        }

        db.executeStatement(
                "SELECT ID FROM `26ArDa_buecher` WHERE ID = "
                        + buchID + ";"
        );

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return;
        }

        QueryResult buch = db.getCurrentQueryResult();

        if (buch == null) {
            System.err.println("Kein Abfrageergebnis erhalten.");
            return;
        }

        if (buch.getRowCount() == 0) {
            System.out.println("Buch wurde nicht gefunden.");
            return;
        }

        db.executeStatement(
                "SELECT ID FROM `26ArDa_ausleihen` " +
                        "WHERE BuchID = " + buchID +
                        " AND Rueckgabedatum IS NULL;"
        );

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return;
        }

        QueryResult ausleihe = db.getCurrentQueryResult();

        if (ausleihe == null) {
            System.err.println("Kein Abfrageergebnis erhalten.");
            return;
        }

        if (ausleihe.getRowCount() > 0) {
            System.out.println("Das Buch ist bereits ausgeliehen.");
            return;
        }

        String sql =
                "INSERT INTO `26ArDa_ausleihen` " +
                        "(MitgliedID, BuchID, Ausleihdatum, FaelligAm, Rueckgabedatum) " +
                        "VALUES (" + mitgliedID + ", " + buchID +
                        ", CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), NULL);";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("Fehler beim Ausleihen: " + db.getErrorMessage());
        } else {
            System.out.println("Buch wurde erfolgreich ausgeliehen.");
        }
    }

    public void buchZurueckgeben(int ausleiheID) {

        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        if (ausleiheID <= 0) {
            System.out.println("Bitte eine positive Ausleihe-ID angeben.");
            return;
        }

        // Prüfen, ob die Ausleihe existiert und noch offen ist
        db.executeStatement(
                "SELECT ID, Rueckgabedatum " +
                        "FROM `26ArDa_ausleihen` " +
                        "WHERE ID = " + ausleiheID + ";"
        );

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return;
        }

        QueryResult ergebnis = db.getCurrentQueryResult();

        if (ergebnis == null || ergebnis.getRowCount() == 0) {
            System.out.println("Ausleihe wurde nicht gefunden.");
            return;
        }

        String[][] daten = ergebnis.getData();

        if (daten[0][1] != null) {
            System.out.println("Das Buch wurde bereits zurückgegeben.");
            return;
        }

        // Rückgabe durchführen
        String update =
                "UPDATE `26ArDa_ausleihen` " +
                        "SET Rueckgabedatum = CURDATE() " +
                        "WHERE ID = " + ausleiheID + ";";

        System.out.println("UPDATE wird ausgeführt: " + update);

        db.executeStatement(update);

        if (db.getErrorMessage() != null) {
            System.err.println(
                    "Fehler bei der Rückgabe: " + db.getErrorMessage()
            );
            return;
        }

        // WICHTIG:
        // Jetzt wirklich nochmal aus der Datenbank lesen
        db.executeStatement(
                "SELECT Rueckgabedatum " +
                        "FROM `26ArDa_ausleihen` " +
                        "WHERE ID = " + ausleiheID + ";"
        );

        if (db.getErrorMessage() != null) {
            System.err.println(
                    "Fehler bei der Kontrolle: " + db.getErrorMessage()
            );
            return;
        }

        QueryResult kontrolle = db.getCurrentQueryResult();

        if (kontrolle == null || kontrolle.getRowCount() == 0) {
            System.out.println("Rückgabe konnte nicht kontrolliert werden.");
            return;
        }

        String rueckgabedatum = kontrolle.getData()[0][0];

        if (rueckgabedatum == null) {
            System.out.println(
                    "FEHLER: Das Rückgabedatum wurde nicht gespeichert."
            );
        } else {
            System.out.println(
                    "Buch wurde erfolgreich zurückgegeben. Datum: "
                            + rueckgabedatum
            );
        }
    }

    public void offeneAusleihenAnzeigen() {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        String sql =
                "SELECT a.ID, m.Vorname, m.Nachname, b.Titel, " +
                        "a.Ausleihdatum, a.FaelligAm " +
                        "FROM `26ArDa_ausleihen` a " +
                        "JOIN `26ArDa_mitglieder` m ON a.MitgliedID = m.ID " +
                        "JOIN `26ArDa_buecher` b ON a.BuchID = b.ID " +
                        "WHERE a.Rueckgabedatum IS NULL " +
                        "ORDER BY a.FaelligAm, a.ID;";

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
            System.out.println("Keine offenen Ausleihen vorhanden.");
            return;
        }

        String[][] daten = ergebnis.getData();

        for (int i = 0; i < daten.length; i++) {
            System.out.println(
                    "Ausleihe " + daten[i][0] +
                            " | Mitglied: " + daten[i][1] + " " + daten[i][2] +
                            " | Buch: " + daten[i][3] +
                            " | Ausgeliehen: " + daten[i][4] +
                            " | Fällig: " + daten[i][5]
            );
        }
    }

    public String[][] meineAusleihenDatenHolen(int mitgliedID) {

        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return new String[0][0];
        }

        String sql =
                "SELECT a.ID, b.Titel, a.Ausleihdatum, " +
                        "a.FaelligAm, a.Rueckgabedatum " +
                        "FROM `26ArDa_ausleihen` a " +
                        "JOIN `26ArDa_buecher` b ON a.BuchID = b.ID " +
                        "WHERE a.MitgliedID = " + mitgliedID + " " +
                        "ORDER BY a.ID DESC;";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return new String[0][0];
        }

        QueryResult ergebnis = db.getCurrentQueryResult();

        if (ergebnis == null) {
            return new String[0][0];
        }

        return ergebnis.getData();
    }

    public String[][] alleAusleihenDatenHolen(boolean nurOffene) {

        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return new String[0][0];
        }

        String sql =
                "SELECT a.ID, m.Vorname, m.Nachname, b.Titel, " +
                        "a.Ausleihdatum, a.FaelligAm, a.Rueckgabedatum " +
                        "FROM `26ArDa_ausleihen` a " +
                        "JOIN `26ArDa_mitglieder` m ON a.MitgliedID = m.ID " +
                        "JOIN `26ArDa_buecher` b ON a.BuchID = b.ID ";

        if (nurOffene) {
            sql += "WHERE a.Rueckgabedatum IS NULL ";
        }

        sql += "ORDER BY a.ID DESC;";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("SQL-Fehler: " + db.getErrorMessage());
            return new String[0][0];
        }

        QueryResult ergebnis = db.getCurrentQueryResult();

        if (ergebnis == null) {
            return new String[0][0];
        }

        return ergebnis.getData();
    }
}