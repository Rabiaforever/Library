package my_project.control;

import KAGO_framework.control.DatabaseController;
import KAGO_framework.control.ViewController;
import KAGO_framework.model.abitur.datenbanken.mysql.QueryResult;
import my_project.model.House;

/**
 * Ein Objekt der Klasse ProgramController dient dazu das Programm zu steuern.
 * Hinweise:
 * - Der Konstruktor sollte nicht geändert werden.
 * - Sowohl die startProgram()- als auch die updateProgram(...)-Methoden müssen vorhanden sein und ihre Signatur sollte
 *   nicht geändert werden
 * - Zusätzliche Methoden sind natürlich gar kein Problem
 */
public class ProgramController {

    //Attribute


    // Referenzen
    private final ViewController viewController;  // diese Referenz soll auf ein Objekt der Klasse viewController zeigen. Über dieses Objekt wird das Fenster gesteuert.
    private DatabaseController db;
    /**
     * Konstruktor
     * Dieser legt das Objekt der Klasse ProgramController an, das den Programmfluss steuert.
     * Damit der ProgramController auf das Fenster zugreifen kann, benötigt er eine Referenz auf das Objekt
     * der Klasse viewController. Diese wird als Parameter übergeben.
     * @param viewController das viewController-Objekt des Programms
     */
    public ProgramController(ViewController viewController){
        this.viewController = viewController;
        db = new DatabaseController("178.254.10.174", "3306", "db85565x2810214", "s85565_2810214", "locker1337SQLproggen!");
        db.connect();
        if (db.getErrorMessage() != null) {
            System.err.println("DB-Verbindungsfehler: " + db.getErrorMessage());
        }
    }

    /**
     * Diese Methode wird genau ein mal nach Programmstart aufgerufen. Hier sollte also alles geregelt werden,
     * was zu diesem Zeipunkt passieren muss.
     */
    public void startProgram() {
        // 1. Daten einfügen


// 2. Abfrage ausführen
        db.executeStatement("SELECT * FROM `26_ArDa`");

// 3. Ergebnis auslesen
        QueryResult res = db.getCurrentQueryResult();

        if (res != null) {
            String[][] data = res.getData();
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    System.out.print(data[i][j] + " ");
                }
                System.out.println();
            }
        } else {
            System.out.println("Fehler: " + db.getErrorMessage());
        }
    }

    /**
     * Diese Methode wird vom ViewController-Objekt automatisch mit jedem Frame aufgerufen (ca. 60mal pro Sekunde)
     * @param dt Zeit seit letztem Frame in Sekunden
     */
    public void updateProgram(double dt){

    }
}
