package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.control.ViewController;

public class ProgramController {

    private final ViewController viewController;
    private final DatabaseController db;
    private final Mitgliederverwaltung mitgliederverwaltung;
    private final Buchverwaltung buchverwaltung;
    private final Ausleihverwaltung ausleihverwaltung;

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

        System.out.println("\nTitelsuche:");
        buchverwaltung.buecherNachTitelSuchen("Test");
    }

    public void updateProgram(double dt) {
    }



}