package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.control.ViewController;
import my_project.view.Login;

public class ProgramController {

    private final ViewController viewController;
    private final DatabaseController db;
    private final Mitgliederverwaltung mitgliederverwaltung;
    private final Buchverwaltung buchverwaltung;
    private final Ausleihverwaltung ausleihverwaltung;
    private Login login;
    private int currentUserID;

    public ProgramController(ViewController viewController) {
        this.viewController = viewController;

        db = new DatabaseController();

        if (!db.connect()) {
            System.err.println("Verbindung zur Datenbank fehlgeschlagen.");
        }

        mitgliederverwaltung = new Mitgliederverwaltung(db);
        buchverwaltung = new Buchverwaltung(db);
        ausleihverwaltung = new Ausleihverwaltung(db);
        login = new Login(this);

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

        System.out.println("\nTitelsuche:");
        buchverwaltung.buecherNachTitelSuchen("Test");
    }

    public void updateProgram(double dt) {
    }

    public boolean login(String name, String passwort){
        currentUserID = mitgliederverwaltung.anmelden(name, passwort);
        return currentUserID >= 0;

    }

    public boolean signUp(String vorname, String nachname, String email, String geburtsdatum, String passwort){
        currentUserID = mitgliederverwaltung.mitgliedAnlegen(vorname, nachname, email, geburtsdatum, passwort);
        return currentUserID >= 0;
    }

}