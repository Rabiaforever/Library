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
        return buchverwaltung.buecherDatenHolen();
    }
    public void buecherSuchen(String suchtext) {
        buchverwaltung.buecherSuchen(suchtext);
    }
    public void mitgliederSuchen(String suchtext) {
        mitgliederverwaltung.mitgliederSuchen(suchtext);
    }
}