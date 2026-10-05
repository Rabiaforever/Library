package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.control.ViewController;
import my_project.view.Login;

import javax.swing.SwingUtilities;

public class ProgramController {

    private final ViewController viewController;
    private final DatabaseController db;
    private final Mitgliederverwaltung mitgliederverwaltung;
    private final Buchverwaltung buchverwaltung;
    private final Ausleihverwaltung ausleihverwaltung;

    private Login login;
    private Mitglied currentUser;

    public ProgramController(ViewController viewController) {
        this.viewController = viewController;

        db = new DatabaseController();

        if (!db.connect()) {
            System.err.println("Verbindung zur Datenbank fehlgeschlagen.");
        }

        mitgliederverwaltung = new Mitgliederverwaltung(db);
        buchverwaltung = new Buchverwaltung(db);
        ausleihverwaltung = new Ausleihverwaltung(db);
    }

    public void startProgram() {
        System.out.println("\nMitglieder:");
        mitgliederverwaltung.mitgliederAnzeigen();

        System.out.println("\nAlle Bücher:");
        buchverwaltung.buecherAnzeigen();

        System.out.println("\nVerfügbare Bücher:");
        buchverwaltung.verfuegbareBuecherAnzeigen();

        System.out.println("\nOffene Ausleihen:");
        ausleihverwaltung.offeneAusleihenAnzeigen();

        System.out.println("\nSuchen nach:");
        buchverwaltung.buecherSuchen("97");

        SwingUtilities.invokeLater(() -> login = new Login(this));
    }

    public void updateProgram(double dt) {
    }

    public boolean login(String name, String passwort) {
        currentUser = mitgliederverwaltung.anmelden(name, passwort);
        if(currentUser!= null){
            System.out.println(currentUser.getId() + " " + currentUser.istAdmin());
        }
        return currentUser != null;
    }

    public boolean signUp(String vorname, String nachname, String email,
                          String geburtsdatum, String passwort) {
        currentUser = mitgliederverwaltung.mitgliedAnlegen(
                vorname, nachname, email, geburtsdatum, passwort
        );

        return currentUser != null;
    }

    public String getCurrentUserName() {
        if (currentUser == null) {
            return "";
        }

        return currentUser.getVorname() + " " + currentUser.getNachname();
    }

    public boolean istAdmin() {
        return currentUser != null && currentUser.istAdmin();
    }

    public String[][] getBuecherDaten() {
        return buchverwaltung.getBuecherDaten();
    }

    public String[][] getBuecherDaten(String suchtext) {
        return buchverwaltung.getBuecherDaten(suchtext);
    }


    public String[][] getMitgliederDaten() {
        return mitgliederverwaltung.getMitgliederDaten();
    }

    public String[][] getMitgliederDaten(String suchtext) {
        return mitgliederverwaltung.getMitgliederDaten(suchtext);
    }

    public String[][] getMeineAusleihenDaten() {
        if (currentUser == null) {
            return new String[0][0];
        }

        return ausleihverwaltung.meineAusleihenDatenHolen(
                currentUser.getId()
        );
    }

    public void buchAusleihen(int buchID) {
        if (currentUser == null) {
            return;
        }

        ausleihverwaltung.buchAusleihen(
                currentUser.getId(),
                buchID
        );
    }

    public void buchZurueckgeben(int ausleiheID) {
        ausleihverwaltung.buchZurueckgeben(ausleiheID);
    }

    public void buchAnlegen(String titel, String isbn,
                            String genre, String standort) {

        if (!istAdmin()) {
            return;
        }

        buchverwaltung.buchAnlegen(titel, isbn, genre, standort);
    }


    public void buchBearbeiten(int id, String titel, String isbn,
                               String genre, String standort) {

        if (!istAdmin()) {
            return;
        }

        buchverwaltung.buchBearbeiten(
                id, titel, isbn, genre, standort
        );
    }


    public void buchLoeschen(int id) {

        if (!istAdmin()) {
            return;
        }

        buchverwaltung.buchLoeschen(id);
    }

    public String[][] getAlleAusleihenDaten(boolean nurOffene) {

        if (!istAdmin()) {
            return new String[0][0];
        }

        return ausleihverwaltung.alleAusleihenDatenHolen(nurOffene);
    }

    public void logout() {
        currentUser = null;
    }

    public void mitgliedBearbeiten(
            int id,
            String vorname,
            String nachname,
            String email,
            String geburtsdatum) {

        if (!istAdmin()) {
            return;
        }

        mitgliederverwaltung.mitgliedBearbeiten(
                id,
                vorname,
                nachname,
                email,
                geburtsdatum
        );
    }


    public void mitgliedLoeschen(int id) {

        if (!istAdmin()) {
            return;
        }

        if (currentUser != null && currentUser.getId() == id) {
            System.out.println(
                    "Du kannst deinen eigenen Account nicht löschen."
            );
            return;
        }

        mitgliederverwaltung.mitgliedLoeschen(id);
    }


    public void rolleAendern(int id, String rolle) {

        if (!istAdmin()) {
            return;
        }

        if (currentUser != null && currentUser.getId() == id) {
            System.out.println(
                    "Du kannst deine eigene Rolle nicht ändern."
            );
            return;
        }

        mitgliederverwaltung.rolleAendern(id, rolle);
    }
}