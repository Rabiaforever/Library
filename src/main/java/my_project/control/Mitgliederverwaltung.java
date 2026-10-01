package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.model.abitur.datenbanken.mysql.QueryResult;

public class Mitgliederverwaltung {

    private final DatabaseController db;

    public Mitgliederverwaltung(DatabaseController db) {
        this.db = db;
    }

    public void mitgliederAnzeigen() {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        db.executeStatement(
                "SELECT * FROM `26ArDa_mitglieder` ORDER BY ID;"
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
            System.out.println("Noch keine Mitglieder vorhanden.");
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

    public void mitgliedAnlegen(String vorname, String nachname,
                                String email, String geburtsdatum) {

        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        if (vorname == null || nachname == null ||
                email == null || geburtsdatum == null) {
            System.err.println("Bitte alle Angaben übergeben.");
            return;
        }

        if (vorname.trim().isEmpty() || nachname.trim().isEmpty()) {
            System.err.println(
                    "Vorname und Nachname dürfen nicht leer sein."
            );
            return;
        }

        String sql =
                "INSERT INTO `26ArDa_mitglieder` "
                        + "(Vorname, Nachname, Email, Geburtsdatum) VALUES ("
                        + "'" + sqlText(vorname) + "', "
                        + "'" + sqlText(nachname) + "', "
                        + "'" + sqlText(email) + "', "
                        + "'" + sqlText(geburtsdatum) + "');";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println(
                    "Fehler beim Anlegen: " + db.getErrorMessage()
            );
        } else {
            System.out.println("Mitglied wurde gespeichert.");
        }
    }

    public void mitgliedSuchen(int id) {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        if (id <= 0) {
            System.out.println("Bitte eine positive Mitglieds-ID angeben.");
            return;
        }

        String sql = "SELECT * FROM `26ArDa_mitglieder` WHERE ID = "
                + id + ";";

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
            System.out.println("Kein Mitglied mit der ID " + id + " gefunden.");
            return;
        }

        String[][] daten = ergebnis.getData();

        for (int spalte = 0; spalte < daten[0].length; spalte++) {
            System.out.print(daten[0][spalte] + " ");
        }

        System.out.println();
    }

    public void emailAendern(int id, String neueEmail) {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        if (id <= 0 || neueEmail == null || neueEmail.trim().isEmpty()) {
            System.out.println("Bitte eine gültige ID und eine E-Mail angeben.");
            return;
        }

        db.executeStatement(
                "SELECT ID FROM `26ArDa_mitglieder` WHERE ID = " + id + ";"
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
            System.out.println("Kein Mitglied mit der ID " + id + " gefunden.");
            return;
        }

        String sql = "UPDATE `26ArDa_mitglieder` SET Email = '"
                + sqlText(neueEmail.trim())
                + "' WHERE ID = " + id + ";";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("Fehler beim Ändern: " + db.getErrorMessage());
        } else {
            System.out.println("E-Mail wurde gespeichert.");
        }
    }


    public void mitgliedLoeschen(int id) {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return;
        }

        if (id <= 0) {
            System.out.println("Bitte eine positive Mitglieds-ID angeben.");
            return;
        }

        db.executeStatement(
                "SELECT ID FROM `26ArDa_mitglieder` WHERE ID = " + id + ";"
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
            System.out.println("Kein Mitglied mit der ID " + id + " gefunden.");
            return;
        }

        db.executeStatement(
                "DELETE FROM `26ArDa_mitglieder` WHERE ID = " + id + ";"
        );

        if (db.getErrorMessage() != null) {
            System.err.println("Fehler beim Löschen: " + db.getErrorMessage());
        } else {
            System.out.println("Mitglied mit der ID " + id + " wurde gelöscht.");
        }
    }

    private String sqlText(String text) {
        return text.replace("\\", "\\\\").replace("'", "''");
    }
}