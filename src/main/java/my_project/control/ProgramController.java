package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.control.ViewController;

public class ProgramController {

    private final ViewController viewController;
    private final DatabaseController db;
    private final Mitgliederverwaltung mitgliederverwaltung;

    public ProgramController(ViewController viewController) {
        this.viewController = viewController;

        db = new DatabaseController();

        if (!db.connect()) {
            System.err.println("Verbindung zur Datenbank fehlgeschlagen.");
        }

        mitgliederverwaltung = new Mitgliederverwaltung(db);
    }

    public void startProgram() {
        mitgliederverwaltung.mitgliederAnzeigen();
    }

    public void updateProgram(double dt) {
    }
}