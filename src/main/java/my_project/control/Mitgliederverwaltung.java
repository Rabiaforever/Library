package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.model.abitur.datenbanken.mysql.QueryResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Mitgliederverwaltung {

    private final DatabaseController db;

    public Mitgliederverwaltung(DatabaseController db) {
        this.db = db;
    }

    public Mitglied anmelden(String name, String passwort) {
        if (name == null || name.trim().isEmpty() ||
                passwort == null || passwort.isEmpty()) {
            System.out.println("Bitte Name und Passwort eingeben.");
            return null;
        }

        String hash = PasswortUtility.hashPassword(passwort);

        if (hash == null) {
            return null;
        }

        QueryResult ergebnis = abfrageAusfuehren(
                "SELECT ID, Vorname, Nachname, Rolle FROM `26ArDa_mitglieder` WHERE Vorname = " +
                        loginSqlWert(name.trim()) + " AND Passwort = " +
                        loginSqlWert(hash) + ";"
        );

        if (ergebnis == null || ergebnis.getRowCount() == 0) {
            return null;
        }

        if (ergebnis.getRowCount() != 1) {
            System.out.println(
                    "Anmeldung nicht eindeutig. Bitte Mitgliedsdaten prüfen."
            );
            return null;
        }

        return new Mitglied(Integer.parseInt(ergebnis.getData()[0][0]), ergebnis.getData()[0][1], ergebnis.getData()[0][2],ergebnis.getData()[0][3]);
    }

    public Mitglied mitgliedAnlegen(String vorname, String nachname,
                               String email, String geburtsdatum,
                               String passwort) {
        if (!verbindungPruefen()) {
            return null;
        }

        if (!mitgliedsdatenPruefen(vorname, nachname, email, geburtsdatum)) {
            return null;
        }

        if (passwort == null || passwort.trim().isEmpty()) {
            System.out.println("Bitte ein Passwort eingeben.");
            return null;
        }

        String hash = PasswortUtility.hashPassword(passwort);

        if (hash == null) {
            return null;
        }

        String sql =
                "INSERT INTO `26ArDa_mitglieder` " +
                        "(Vorname, Nachname, Email, Geburtsdatum, Passwort, Rolle) VALUES (" +
                        loginSqlWert(vorname.trim()) + ", " +
                        loginSqlWert(nachname.trim()) + ", " +
                        loginSqlWert(email == null ? null : email.trim()) + ", " +
                        loginSqlWert(geburtsdatum == null ? null : geburtsdatum.trim()) + ", " +
                        loginSqlWert(hash) + ", 'Mitglied');";

        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println("Fehler beim Anlegen: " + db.getErrorMessage());
            return null;
        }

        QueryResult ergebnis = abfrageAusfuehren("SELECT LAST_INSERT_ID();");

        if (ergebnis == null || ergebnis.getRowCount() != 1) {
            return null;
        }

        System.out.println("Mitglied wurde gespeichert.");
        return  anmelden(vorname, passwort);
    }

    public void mitgliederAnzeigen() {
        QueryResult ergebnis = abfrageAusfuehren(
                "SELECT ID, Vorname, Nachname, Email, Geburtsdatum " +
                        "FROM `26ArDa_mitglieder` ORDER BY ID;"
        );

        ergebnisAnzeigen(ergebnis, "Noch keine Mitglieder vorhanden.");
    }

    public void mitgliedAnlegen(String vorname, String nachname,
                                String email, String geburtsdatum) {
        if (!verbindungPruefen()) {
            return;
        }

        if (!mitgliedsdatenPruefen(vorname, nachname, email, geburtsdatum)) {
            return;
        }

        String sql =
                "INSERT INTO `26ArDa_mitglieder` " +
                        "(Vorname, Nachname, Email, Geburtsdatum) VALUES (" +
                        sqlWert(vorname) + ", " +
                        sqlWert(nachname) + ", " +
                        sqlWert(email) + ", " +
                        sqlWert(geburtsdatum) + ");";

        aenderungAusfuehren(sql, "Mitglied wurde gespeichert.");
    }

    public void mitgliedBearbeiten(int id, String vorname, String nachname,
                                   String email, String geburtsdatum) {
        if (!verbindungPruefen()) {
            return;
        }

        if (!idPruefen(id)) {
            return;
        }

        if (!mitgliedsdatenPruefen(vorname, nachname, email, geburtsdatum)) {
            return;
        }

        if (!mitgliedVorhanden(id)) {
            return;
        }

        String sql =
                "UPDATE `26ArDa_mitglieder` SET " +
                        "Vorname = " + sqlWert(vorname) + ", " +
                        "Nachname = " + sqlWert(nachname) + ", " +
                        "Email = " + sqlWert(email) + ", " +
                        "Geburtsdatum = " + sqlWert(geburtsdatum) + " " +
                        "WHERE ID = " + id + ";";

        aenderungAusfuehren(sql, "Mitgliedsdaten wurden gespeichert.");
    }

    public void mitgliedSuchen(int id) {
        if (!idPruefen(id)) {
            return;
        }

        QueryResult ergebnis = abfrageAusfuehren(
                "SELECT ID, Vorname, Nachname, Email, Geburtsdatum " +
                        "FROM `26ArDa_mitglieder` WHERE ID = " + id + ";"
        );

        ergebnisAnzeigen(
                ergebnis,
                "Kein Mitglied mit der ID " + id + " gefunden."
        );
    }

    public void emailAendern(int id, String neueEmail) {
        if (!verbindungPruefen()) {
            return;
        }

        if (!idPruefen(id) || !emailPruefen(neueEmail)) {
            return;
        }

        if (!mitgliedVorhanden(id)) {
            return;
        }

        String sql =
                "UPDATE `26ArDa_mitglieder` " +
                        "SET Email = " + sqlWert(neueEmail) +
                        " WHERE ID = " + id + ";";

        aenderungAusfuehren(sql, "E-Mail wurde gespeichert.");
    }

    public void mitgliedLoeschen(int id) {
        if (!verbindungPruefen()) {
            return;
        }

        if (!idPruefen(id)) {
            return;
        }

        if (!mitgliedVorhanden(id)) {
            return;
        }

        QueryResult ausleihen = abfrageAusfuehren(
                "SELECT ID FROM `26ArDa_ausleihen` " +
                        "WHERE MitgliedID = " + id + " LIMIT 1;"
        );

        if (ausleihen == null) {
            return;
        }

        if (ausleihen.getRowCount() > 0) {
            System.out.println(
                    "Das Mitglied kann nicht gelöscht werden, " +
                            "weil Ausleihen darauf verweisen."
            );
            return;
        }

        String sql =
                "DELETE FROM `26ArDa_mitglieder` WHERE ID = " + id + ";";

        aenderungAusfuehren(
                sql,
                "Mitglied mit der ID " + id + " wurde gelöscht."
        );
    }

    private boolean verbindungPruefen() {
        if (!db.isConnected()) {
            System.err.println("Keine Datenbankverbindung.");
            return false;
        }

        return true;
    }

    private boolean idPruefen(int id) {
        if (id <= 0) {
            System.out.println("Bitte eine positive Mitglieds-ID angeben.");
            return false;
        }

        return true;
    }

    private boolean mitgliedVorhanden(int id) {
        QueryResult ergebnis = abfrageAusfuehren(
                "SELECT ID FROM `26ArDa_mitglieder` WHERE ID = " + id + ";"
        );

        if (ergebnis == null) {
            return false;
        }

        if (ergebnis.getRowCount() == 0) {
            System.out.println("Kein Mitglied mit der ID " + id + " gefunden.");
            return false;
        }

        return true;
    }

    private boolean mitgliedsdatenPruefen(String vorname, String nachname,
                                          String email, String geburtsdatum) {
        if (vorname == null || vorname.trim().isEmpty() ||
                nachname == null || nachname.trim().isEmpty()) {
            System.out.println("Vorname und Nachname dürfen nicht leer sein.");
            return false;
        }

        if (vorname.trim().length() > 50 || nachname.trim().length() > 50) {
            System.out.println(
                    "Vorname und Nachname dürfen jeweils höchstens 50 Zeichen haben."
            );
            return false;
        }

        if (!emailPruefen(email)) {
            return false;
        }

        return geburtsdatumPruefen(geburtsdatum);
    }

    private boolean emailPruefen(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }

        String adresse = email.trim();

        if (adresse.length() > 150) {
            System.out.println("Die E-Mail darf höchstens 150 Zeichen haben.");
            return false;
        }

        if (!adresse.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            System.out.println(
                    "Bitte eine E-Mail im Format name@example.com eingeben."
            );
            return false;
        }

        return true;
    }

    private boolean geburtsdatumPruefen(String geburtsdatum) {
        if (geburtsdatum == null || geburtsdatum.trim().isEmpty()) {
            return true;
        }

        String eingabe = geburtsdatum.trim();

        if (!eingabe.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            System.out.println(
                    "Bitte das Geburtsdatum im Format JJJJ-MM-TT eingeben."
            );
            return false;
        }

        try {
            LocalDate datum = LocalDate.parse(eingabe);

            if (datum.getYear() < 1000 || datum.isAfter(LocalDate.now())) {
                System.out.println(
                        "Das Geburtsdatum muss zwischen dem Jahr 1000 und heute liegen."
                );
                return false;
            }
        } catch (DateTimeParseException e) {
            System.out.println("Das angegebene Geburtsdatum existiert nicht.");
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

    private void aenderungAusfuehren(String sql, String erfolgsmeldung) {
        db.executeStatement(sql);

        if (db.getErrorMessage() != null) {
            System.err.println(
                    "Fehler beim Speichern oder Löschen: " + db.getErrorMessage()
            );
        } else {
            System.out.println(erfolgsmeldung);
        }
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
                    "Mitglieds-ID: " + daten[i][0] +
                            " | Vorname: " + daten[i][1] +
                            " | Nachname: " + daten[i][2] +
                            " | E-Mail: " + daten[i][3] +
                            " | Geburtsdatum: " + daten[i][4]
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

    private String loginSqlWert(String text) {
        if (text == null || text.isEmpty()) {
            return "NULL";
        }

        StringBuilder hex = new StringBuilder();

        for (byte zeichen : text.getBytes(StandardCharsets.UTF_8)) {
            hex.append(String.format("%02x", zeichen & 0xff));
        }

        return "CONVERT(X'" + hex + "' USING utf8mb4)";
    }
    public void mitgliederSuchen(String suchtext) {
        if (suchtext == null || suchtext.trim().isEmpty()) {
            System.out.println("Bitte einen Suchtext eingeben.");
            return;
        }

        String sql =
                "SELECT ID, Vorname, Nachname, E-Mail, Geburtsdatum " +
                        "FROM `26ArDa_mitglieder` " +
                        "WHERE Vorname LIKE '%" + suchtext.trim() + "%' " +
                        "OR ID = '" + suchtext.trim() + "' " +
                        "OR Nachname LIKE '%" + suchtext.trim() + "%';";

        QueryResult ergebnis = abfrageAusfuehren(sql);

        ergebnisAnzeigen(ergebnis, "Keine passenden Bücher gefunden.");
    }

    // Alle Mitglieder laden
    public String[][] getMitgliederDaten() {

        String sql =
                "SELECT ID, Vorname, Nachname, Email, Geburtsdatum " +
                        "FROM `26ArDa_mitglieder` " +
                        "ORDER BY ID;";

        QueryResult ergebnis = abfrageAusfuehren(sql);

        if (ergebnis == null || ergebnis.getRowCount() == 0) {
            return new String[0][0];
        }

        return ergebnis.getData();
    }


    // Mitglieder suchen
    public String[][] getMitgliederDaten(String suchtext) {

        if (suchtext == null || suchtext.trim().isEmpty()) {
            return new String[0][0];
        }

        suchtext = suchtext.trim();

        String sql =
                "SELECT ID, Vorname, Nachname, Email, Geburtsdatum " +
                        "FROM `26ArDa_mitglieder` " +
                        "WHERE Vorname LIKE '%" + suchtext + "%' " +
                        "OR Nachname LIKE '%" + suchtext + "%' " +
                        "OR Email LIKE '%" + suchtext + "%' " +
                        "OR ID = '" + suchtext + "' " +
                        "ORDER BY ID;";

        QueryResult ergebnis = abfrageAusfuehren(sql);

        if (ergebnis == null || ergebnis.getRowCount() == 0) {
            return new String[0][0];
        }

        return ergebnis.getData();
    }

    public void rolleAendern(int id, String rolle) {

        if (!verbindungPruefen()) {
            return;
        }

        if (!idPruefen(id)) {
            return;
        }

        if (!mitgliedVorhanden(id)) {
            return;
        }

        if (!rolle.equals("Admin") && !rolle.equals("Mitglied")) {
            System.out.println("Ungültige Rolle.");
            return;
        }

        String sql =
                "UPDATE `26ArDa_mitglieder` " +
                        "SET Rolle = " + sqlWert(rolle) +
                        " WHERE ID = " + id + ";";

        aenderungAusfuehren(
                sql,
                "Rolle wurde geändert."
        );
    }
}